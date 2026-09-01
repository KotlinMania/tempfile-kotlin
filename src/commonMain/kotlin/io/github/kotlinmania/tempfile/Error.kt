// port-lint: source error.rs
package io.github.kotlinmania.tempfile

/**
 * Path error wrapper matching upstream `error::PathError`.
 */
internal class PathError(
    val path: String,
    val err: IoException,
) : RuntimeException("$err at path \"$path\"", err.cause) {
    /**
     * Formatting helper matching upstream `Display::fmt`.
     */
    fun fmt(): String = toString()

    /**
     * Source cause matching upstream `Error::source`.
     */
    fun source(): Throwable? = err.cause

    override fun toString(): String = "$err at path \"$path\""
}

/**
 * Extension trait matching upstream `IoResultExt<T>`.
 */
internal interface IoResultExt<T> {
    /**
     * Attaches path context to any IO failure.
     */
    fun withErrPath(path: () -> String): Result<T>
}

/**
 * Attaches a path to an [IoException] inside a [Result], matching upstream withErrPath.
 */
internal fun <T> Result<T>.withErrPath(path: () -> String): Result<T> {
    val e = exceptionOrNull() ?: return this
    if (e !is IoException) return this
    return Result.failure(IoException(e.kind, PathError(path(), e)))
}
