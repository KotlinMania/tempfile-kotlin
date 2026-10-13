// port-lint: source spooled.rs
package io.github.kotlinmania.tempfile

import io.github.kotlinmania.tempfile.file.NamedTempFile
import io.github.kotlinmania.tempfile.file.writeBytes

/**
 * Enumeration of possible methods to seek within an I/O stream.
 */
public sealed class SeekFrom {
    public data class Start(
        public val position: Long,
    ) : SeekFrom()

    public data class End(
        public val offset: Long,
    ) : SeekFrom()

    public data class Current(
        public val offset: Long,
    ) : SeekFrom()
}

/**
 * A wrapper for the two states of a [SpooledTempFile]. Either:
 * 1. An in-memory buffer.
 * 2. A temporary file on disk.
 */
public sealed class SpooledData {
    public data class InMemory(
        public val buffer: ByteArray,
    ) : SpooledData() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is InMemory) return false
            return buffer.contentEquals(other.buffer)
        }

        override fun hashCode(): Int = buffer.contentHashCode()
    }

    public data class OnDisk(
        public val path: String,
    ) : SpooledData()
}

/**
 * An object that behaves like a regular temporary file, but keeps data in
 * memory until it reaches a configured size, at which point the data is
 * written to a temporary file on disk, and further operations use the file
 * on disk.
 */
public class SpooledTempFile(
    public val maxSize: Int,
    public val dir: String? = null,
) {
    private var data: ByteArray = ByteArray(0)
    private var length: Int = 0
    private var position: Long = 0L
    private var rolled: Boolean = false
    private var diskPath: String? = null
    private var tempFileHandle: NamedTempFile<String>? = null

    /**
     * Returns true if the file has been rolled over to disk.
     */
    public fun isRolled(): Boolean = rolled

    /**
     * Rolls over to a file on disk, regardless of current size. Does nothing if already rolled over.
     */
    public fun roll(): Result<Unit> {
        if (!rolled) {
            val res = cursorToTempfile(data.copyOf(length), dir)
            if (res.isFailure) return Result.failure(res.exceptionOrNull() ?: IoException(IoErrorKind.Other, "roll failed"))
            val tf = res.getOrThrow()
            tempFileHandle = tf
            diskPath = tf.path()
            rolled = true
        }
        return Result.success(Unit)
    }

    /**
     * Truncate the file to the specified size.
     */
    public fun setLen(size: Long): Result<Unit> {
        if (size > maxSize.toLong() && !rolled) {
            val rollRes = roll()
            if (rollRes.isFailure) return rollRes
        }
        val targetSize = size.toInt()
        data = data.copyOf(targetSize)
        length = targetSize
        if (rolled) {
            tempFileHandle?.let { tf ->
                writeBytes(tf.path(), data.copyOf(length))
            }
        }
        return Result.success(Unit)
    }

    /**
     * Consumes and returns the inner [SpooledData].
     */
    public fun intoInner(): SpooledData {
        val currentDiskPath = diskPath
        return if (rolled && currentDiskPath != null) {
            SpooledData.OnDisk(currentDiskPath)
        } else {
            SpooledData.InMemory(data.copyOf(length))
        }
    }

    /**
     * Convert into a regular temporary file, writing it to disk if necessary.
     */
    public fun intoFile(): Result<NamedTempFile<String>> {
        if (!rolled) {
            val res = roll()
            if (res.isFailure) return Result.failure(res.exceptionOrNull() ?: IoException(IoErrorKind.Other, "roll failed"))
        }
        val tf = tempFileHandle ?: return Result.failure(IoException(IoErrorKind.NotFound, "file not found"))
        return Result.success(tf)
    }

    /**
     * Close the underlying temporary file if rolled over.
     */
    public fun close(): Result<Unit> {
        val tf = tempFileHandle
        tempFileHandle = null
        diskPath = null
        return tf?.close() ?: Result.success(Unit)
    }

    public fun drop(): Result<Unit> = close()

    /**
     * Reads bytes into the destination buffer.
     */
    public fun read(buf: ByteArray): Int {
        if (position >= length) return 0
        val toRead = minOf(buf.size, (length - position).toInt())
        data.copyInto(buf, destinationOffset = 0, startIndex = position.toInt(), endIndex = position.toInt() + toRead)
        position += toRead
        return toRead
    }

    /**
     * Reads into multiple byte buffers.
     */
    public fun readVectored(bufs: List<ByteArray>): Int {
        var total = 0
        for (buf in bufs) {
            val readCount = read(buf)
            total += readCount
            if (readCount < buf.size) break
        }
        return total
    }

    /**
     * Reads all remaining bytes to end into a byte list.
     */
    public fun readToEnd(buf: MutableList<Byte>): Int {
        var count = 0
        while (position < length) {
            buf.add(data[position.toInt()])
            position++
            count++
        }
        return count
    }

    /**
     * Reads all remaining bytes as a UTF-8 string into [buf].
     */
    public fun readToString(buf: StringBuilder): Int {
        if (position >= length) return 0
        val remaining = data.copyOfRange(position.toInt(), length)
        val text = remaining.decodeToString()
        buf.append(text)
        val count = remaining.size
        position = length.toLong()
        return count
    }

    /**
     * Reads the exact number of bytes required to fill [buf].
     */
    public fun readExact(buf: ByteArray): Result<Unit> {
        if (position + buf.size > length) {
            return Result.failure(IoException(IoErrorKind.UnexpectedEof, "failed to fill whole buffer"))
        }
        val count = read(buf)
        return if (count == buf.size) {
            Result.success(Unit)
        } else {
            Result.failure(IoException(IoErrorKind.UnexpectedEof, "failed to fill whole buffer"))
        }
    }

    /**
     * Writes the given bytes into the spooled temporary file.
     */
    public fun write(bytes: ByteArray): Int {
        if (position > Int.MAX_VALUE.toLong() - bytes.size) {
            throw IoException(IoErrorKind.InvalidInput, "write overflow")
        }
        val endPos = (position + bytes.size).toInt()
        if (endPos > maxSize && !rolled) {
            roll().getOrThrow()
        }
        if (endPos > data.size) {
            data = data.copyOf(maxOf(endPos, data.size * 2))
        }
        bytes.copyInto(data, destinationOffset = position.toInt())
        position = endPos.toLong()
        if (endPos > length) {
            length = endPos
        }
        if (rolled) {
            tempFileHandle?.let { tf ->
                writeBytes(tf.path(), data.copyOf(length))
            }
        }
        return bytes.size
    }

    /**
     * Writes multiple buffers.
     */
    public fun writeVectored(bufs: List<ByteArray>): Int {
        var total = 0
        for (buf in bufs) {
            total += write(buf)
        }
        return total
    }

    /**
     * Flushes written data.
     */
    public fun flush(): Result<Unit> = Result.success(Unit)

    /**
     * Seeks to a given position using [SeekFrom].
     */
    public fun seek(pos: SeekFrom): Result<Long> {
        val newPos =
            when (pos) {
                is SeekFrom.Start -> pos.position
                is SeekFrom.End -> length.toLong() + pos.offset
                is SeekFrom.Current -> position + pos.offset
            }
        if (newPos < 0) {
            return Result.failure(
                IoException(IoErrorKind.InvalidInput, "invalid seek to a negative or overflowing position"),
            )
        }
        position = newPos
        return Result.success(position)
    }

    /**
     * Seeks to a given offset from start.
     */
    public fun seek(offset: Long): Long = seek(SeekFrom.Start(offset)).getOrThrow()

    /**
     * Returns the current stream position as a [Result].
     */
    public fun streamPosition(): Result<Long> = Result.success(position)

    /**
     * Current position in the spooled file.
     */
    public fun position(): Long = position

    companion object {
        public fun new(maxSize: Int): SpooledTempFile = SpooledTempFile(maxSize)

        public fun newIn(maxSize: Int, dir: String): SpooledTempFile = SpooledTempFile(maxSize, dir)
    }
}

/**
 * Write a cursor buffer into a temporary file.
 */
internal fun cursorToTempfile(bytes: ByteArray, dir: String?): Result<NamedTempFile<String>> {
    val fileRes =
        if (dir != null) {
            NamedTempFile.newIn(dir)
        } else {
            NamedTempFile.new()
        }
    return fileRes.flatMap { tf ->
        writeBytes(tf.path(), bytes).map { tf }
    }
}

/**
 * Creates a new [SpooledTempFile] with the specified max size in bytes.
 */
public fun spooledTempfile(maxSize: Int): SpooledTempFile = SpooledTempFile.new(maxSize)

/**
 * Creates a new [SpooledTempFile] backed by the specified directory.
 */
public fun spooledTempfileIn(maxSize: Int, dir: String): SpooledTempFile = SpooledTempFile.newIn(maxSize, dir)

private inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(onSuccess = transform, onFailure = { Result.failure(it) })
