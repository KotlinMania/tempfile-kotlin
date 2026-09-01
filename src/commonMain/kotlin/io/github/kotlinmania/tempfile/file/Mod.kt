// port-lint: source file/mod.rs
package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.Builder
import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.currentDir
import io.github.kotlinmania.tempfile.joinPath
import io.github.kotlinmania.tempfile.tempDir
import io.github.kotlinmania.tempfile.withErrPath

/**
 * Create a new temporary file in the default temp directory.
 */
fun tempfile(): Result<NamedTempFile<String>> = tempfileIn(tempDir())

/**
 * Create a new temporary file in the specified directory.
 */
fun tempfileIn(dir: String): Result<NamedTempFile<String>> =
    NamedTempFile.newIn(dir)

/**
 * Error returned when persisting a temporary file path fails.
 */
class PathPersistError(
    val error: Throwable,
    val path: TempPath,
) : RuntimeException("failed to persist temporary file path: $error", error) {
    fun fmt(): String = toString()

    fun source(): Throwable? = error

    override fun toString(): String = "failed to persist temporary file path: $error"
}

/**
 * Error returned when persisting a temporary file fails.
 */
class PersistError(
    val error: Throwable,
    val file: Any?,
) : RuntimeException("failed to persist temporary file: $error", error) {
    fun fmt(): String = toString()

    fun source(): Throwable? = error

    override fun toString(): String = "failed to persist temporary file: $error"
}

/**
 * A path to a named temporary file without an open file handle.
 */
class TempPath internal constructor(
    private var pathValue: String,
    var disableCleanup: Boolean = false,
) {
    /** Accesses the path to the temporary file. */
    fun path(): String = pathValue

    /** Accesses the path to the temporary file, matching upstream `AsRef<Path>`. */
    fun asRef(): String = pathValue

    /** Accesses the path to the temporary file. */
    fun asPath(): String = pathValue

    /** Close and remove the temporary file. */
    fun close(): Result<Unit> {
        if (pathValue.isEmpty()) return Result.success(Unit)
        val target = pathValue
        pathValue = ""
        if (disableCleanup) return Result.success(Unit)
        return removeFile(target).withErrPath { target }
    }

    /**
     * Persist the temporary file at the target path, replacing any existing file.
     */
    fun persist(newPath: String): Result<Unit> {
        val target = pathValue
        val res = persistFile(target, newPath, overwrite = true)
        return if (res.isSuccess) {
            pathValue = ""
            Result.success(Unit)
        } else {
            Result.failure(PathPersistError(res.exceptionOrNull() ?: IoException(IoErrorKind.Other, "persist failed"), this))
        }
    }

    /**
     * Persist the temporary file at the target path if no file exists there.
     */
    fun persistNoclobber(newPath: String): Result<Unit> {
        val target = pathValue
        val res = persistFile(target, newPath, overwrite = false)
        return if (res.isSuccess) {
            pathValue = ""
            Result.success(Unit)
        } else {
            Result.failure(PathPersistError(res.exceptionOrNull() ?: IoException(IoErrorKind.Other, "persist noclobber failed"), this))
        }
    }

    /**
     * Keep the temporary file from being deleted without moving it.
     */
    fun keep(): Result<String> {
        val target = pathValue
        val res = keepFile(target)
        return if (res.isSuccess) {
            disableCleanup = true
            pathValue = ""
            Result.success(target)
        } else {
            Result.failure(PathPersistError(res.exceptionOrNull() ?: IoException(IoErrorKind.Other, "keep failed"), this))
        }
    }

    /** Disable cleanup of the temporary file. */
    fun disableCleanup(disableCleanup: Boolean) {
        this.disableCleanup = disableCleanup
    }

    fun drop(): Result<Unit> = close()

    fun fmt(): String = toString()

    override fun toString(): String = "TempPath(path=$pathValue)"

    companion object {
        @Deprecated("use tryFromPath instead", ReplaceWith("tryFromPath(path)"))
        fun fromPath(path: String): TempPath {
            var p = path
            if (p.isNotEmpty() && !p.startsWith("/") && !p.contains(":\\")) {
                currentDir()?.let { cwd ->
                    p = joinPath(cwd, p)
                }
            }
            return TempPath(p, disableCleanup = false)
        }

        fun tryFromPath(path: String): Result<TempPath> {
            if (path.isEmpty()) {
                return Result.failure(IoException(IoErrorKind.InvalidInput, "cannot construct a TempPath from an empty path"))
            }
            var p = path
            if (!p.startsWith("/") && !p.contains(":\\")) {
                val cwd =
                    currentDir()
                        ?: return Result.failure(IoException(IoErrorKind.NotFound, "cannot determine current working directory"))
                p = joinPath(cwd, p)
            }
            return Result.success(TempPath(p, disableCleanup = false))
        }

        internal fun new(path: String, disableCleanup: Boolean): TempPath =
            TempPath(path, disableCleanup)
    }
}

/**
 * A named temporary file.
 */
class NamedTempFile<F>(
    private var pathHandle: TempPath,
    private var fileHandle: F,
) {
    /** Get the temporary file's path. */
    fun path(): String = pathHandle.path()

    /** Accesses the path, matching upstream `AsRef<Path>`. */
    fun asRef(): String = pathHandle.path()

    /** Accesses the path. */
    fun asPath(): String = pathHandle.path()

    /** Get a reference to the underlying file handle. */
    fun asFile(): F = fileHandle

    /** Get a mutable reference to the underlying file handle. */
    fun asFileMut(): F = fileHandle

    /** Turns this into an unmanaged file handle. */
    fun intoFile(): F = fileHandle

    /** Closes the file, leaving only the temporary file path. */
    fun intoTempPath(): TempPath = pathHandle

    /** Converts into its constituent parts. */
    fun intoParts(): Pair<F, TempPath> = Pair(fileHandle, pathHandle)

    /** Close and remove the temporary file. */
    fun close(): Result<Unit> = pathHandle.close()

    /** Persist the temporary file at the target path, replacing any existing file. */
    fun persist(newPath: String): Result<F> {
        val res = pathHandle.persist(newPath)
        return if (res.isSuccess) {
            Result.success(fileHandle)
        } else {
            val err = res.exceptionOrNull()
            Result.failure(PersistError(err ?: IoException(IoErrorKind.Other, "persist failed"), this))
        }
    }

    /** Persist the temporary file at the target path if no file exists there. */
    fun persistNoclobber(newPath: String): Result<F> {
        val res = pathHandle.persistNoclobber(newPath)
        return if (res.isSuccess) {
            Result.success(fileHandle)
        } else {
            val err = res.exceptionOrNull()
            Result.failure(PersistError(err ?: IoException(IoErrorKind.Other, "persist noclobber failed"), this))
        }
    }

    /** Keep the temporary file from being deleted without moving it. */
    fun keep(): Result<Pair<F, String>> {
        val res = pathHandle.keep()
        return if (res.isSuccess) {
            Result.success(Pair(fileHandle, res.getOrThrow()))
        } else {
            val err = res.exceptionOrNull()
            Result.failure(PersistError(err ?: IoException(IoErrorKind.Other, "keep failed"), this))
        }
    }

    /** Disable cleanup of the temporary file. */
    fun disableCleanup(disableCleanup: Boolean) {
        pathHandle.disableCleanup(disableCleanup)
    }

    /** Reopen the temporary file. */
    fun reopen(): Result<F> = Result.success(fileHandle)

    fun drop(): Result<Unit> = close()

    fun fmt(): String = toString()

    override fun toString(): String = "NamedTempFile(${pathHandle.path()})"

    companion object {
        fun new(): Result<NamedTempFile<String>> = Builder().tempfile()

        fun newIn(dir: String): Result<NamedTempFile<String>> = Builder().tempfileIn(dir)

        fun withPrefix(prefix: String): Result<NamedTempFile<String>> =
            Builder().prefix(prefix).tempfile()

        fun withSuffix(suffix: String): Result<NamedTempFile<String>> =
            Builder().suffix(suffix).tempfile()

        fun withPrefixIn(prefix: String, dir: String): Result<NamedTempFile<String>> =
            Builder().prefix(prefix).tempfileIn(dir)

        fun withSuffixIn(suffix: String, dir: String): Result<NamedTempFile<String>> =
            Builder().suffix(suffix).tempfileIn(dir)

        fun <F> fromParts(file: F, path: TempPath): NamedTempFile<F> =
            NamedTempFile(path, file)
    }
}

/**
 * Creates a named temporary file at [path].
 */
internal fun createNamed(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<NamedTempFile<String>> =
    createNamedFile(path)
        .withErrPath { path }
        .map {
            NamedTempFile(
                TempPath.new(path, disableCleanup),
                path,
            )
        }
