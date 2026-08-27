// port-lint: source tempfile/src/error.rs
package io.github.kotlinmania.tempfile

private class PathError(
    path: String,
    err: IoException,
) : RuntimeException("$err at path \"$path\"", err.cause) {
    override fun toString(): String = message ?: ""
}

internal fun <T> Result<T>.withErrPath(path: () -> String): Result<T> {
    val e = exceptionOrNull() ?: return this
    if (e !is IoException) return this
    return Result.failure(IoException(e.kind, PathError(path(), e)))
}
