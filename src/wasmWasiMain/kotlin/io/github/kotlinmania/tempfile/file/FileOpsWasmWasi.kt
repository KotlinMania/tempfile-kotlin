package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

private val createdFiles: MutableMap<String, ByteArray> = mutableMapOf()

internal actual fun createNamedFile(path: String): Result<Unit> {
    if (path in createdFiles) {
        return Result
            .failure<Unit>(
                IoException(IoErrorKind.AlreadyExists, "createNamedFile: $path already exists"),
            ).withErrPath { path }
    }
    createdFiles[path] = ByteArray(0)
    return Result.success(Unit)
}

internal actual fun removeFile(path: String): Result<Unit> {
    if (createdFiles.remove(path) != null) {
        return Result.success(Unit)
    }
    return Result
        .failure<Unit>(
            IoException(IoErrorKind.NotFound, "removeFile: $path not found"),
        ).withErrPath { path }
}

internal actual fun fileExists(path: String): Boolean =
    if (path.isEmpty()) false else path in createdFiles


internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> {
    val data = createdFiles[oldPath] ?: return Result
        .failure<Unit>(
            IoException(IoErrorKind.NotFound, "persistFile: $oldPath not found"),
        ).withErrPath { oldPath }

    if (!overwrite && newPath in createdFiles) {
        return Result
            .failure<Unit>(
                IoException(IoErrorKind.AlreadyExists, "persistFile: $newPath already exists"),
            ).withErrPath { newPath }
    }
    createdFiles.remove(oldPath)
    createdFiles[newPath] = data
    return Result.success(Unit)
}

internal actual fun keepFile(path: String): Result<Unit> = Result.success(Unit)

internal actual fun readBytes(path: String): Result<ByteArray> {
    val data = createdFiles[path] ?: return Result
        .failure<ByteArray>(
            IoException(IoErrorKind.NotFound, "readBytes: $path not found"),
        ).withErrPath { path }
    return Result.success(data)
}

internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> {
    createdFiles[path] = bytes
    return Result.success(Unit)
}
