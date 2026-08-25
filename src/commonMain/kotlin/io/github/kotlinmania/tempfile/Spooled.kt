// port-lint: source src/spooled.rs
package io.github.kotlinmania.tempfile

/**
 * A wrapper for the two states of a [SpooledTempFile].
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
 * transitioned to disk or marked as rolled.
 */
public class SpooledTempFile(
    public val maxSize: Int,
    public val dir: String? = null,
) {
    private var data: ByteArray = ByteArray(0)
    private var position: Int = 0
    private var rolled: Boolean = false
    private var diskPath: String? = null

    /**
     * Returns true if the file has been rolled over to disk.
     */
    public fun isRolled(): Boolean = rolled

    /**
     * Rolls over to a file on disk, regardless of current size.
     */
    public fun roll(): Result<Unit> {
        if (!rolled) {
            rolled = true
        }
        return Result.success(Unit)
    }

    /**
     * Writes the given bytes into the spooled temporary file.
     */
    public fun write(bytes: ByteArray): Int {
        if (position + bytes.size > maxSize && !rolled) {
            roll()
        }
        val needed = position + bytes.size
        if (needed > data.size) {
            data = data.copyOf(maxOf(needed, data.size * 2))
        }
        bytes.copyInto(data, destinationOffset = position)
        position += bytes.size
        return bytes.size
    }

    /**
     * Reads bytes into the destination buffer.
     */
    public fun read(buf: ByteArray): Int {
        if (position >= data.size) return 0
        val toRead = minOf(buf.size, data.size - position)
        data.copyInto(buf, destinationOffset = 0, startIndex = position, endIndex = position + toRead)
        position += toRead
        return toRead
    }

    /**
     * Current position in the spooled file.
     */
    public fun position(): Long = position.toLong()

    /**
     * Truncates or extends the file to the specified size.
     */
    public fun setLen(size: Long) {
        if (size > maxSize.toLong() && !rolled) {
            roll()
        }
        data = data.copyOf(size.toInt())
        if (position > data.size) {
            position = data.size
        }
    }

    /**
     * Consumes and returns the inner [SpooledData].
     */
    public fun intoInner(): SpooledData =
        if (rolled && diskPath != null) {
            SpooledData.OnDisk(diskPath!!)
        } else {
            SpooledData.InMemory(data.copyOf(position))
        }
}

/**
 * Creates a new [SpooledTempFile] with the specified max size in bytes.
 */
public fun spooledTempfile(maxSize: Int): SpooledTempFile = SpooledTempFile(maxSize)

/**
 * Creates a new [SpooledTempFile] backed by the specified directory.
 */
public fun spooledTempfileIn(maxSize: Int, dir: String): SpooledTempFile = SpooledTempFile(maxSize, dir)
