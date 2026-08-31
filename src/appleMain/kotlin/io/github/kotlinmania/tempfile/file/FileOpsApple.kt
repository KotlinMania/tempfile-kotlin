package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager

internal expect fun appleReadBytes(path: String): Result<ByteArray>
internal expect fun appleWriteBytes(path: String, bytes: ByteArray): Result<Unit>

@OptIn(ExperimentalForeignApi::class)
internal actual fun createNamedFile(path: String): Result<Unit> {
    if (path.isEmpty()) return Result.failure<Unit>(IoException(IoErrorKind.NotFound, "path is empty")).withErrPath { path }
    if (fileExists(path)) return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "file already exists")).withErrPath { path }
    val created = NSFileManager.defaultManager.createFileAtPath(path, null, null)
    if (created) return Result.success(Unit)
    return Result.failure<Unit>(IoException(IoErrorKind.Other, "failed to create file at $path")).withErrPath { path }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun removeFile(path: String): Result<Unit> {
    if (path.isEmpty()) return Result.failure<Unit>(IoException(IoErrorKind.NotFound, "path is empty")).withErrPath { path }
    return runCatching {
        val success = NSFileManager.defaultManager.removeItemAtPath(path, null)
        if (!success) {
            throw IoException(IoErrorKind.NotFound, "failed to remove file at $path")
        }
    }.withErrPath { path }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun fileExists(path: String): Boolean =
    if (path.isEmpty()) false else NSFileManager.defaultManager.fileExistsAtPath(path)

@OptIn(ExperimentalForeignApi::class)
internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> {
    if (overwrite && fileExists(newPath)) {
        NSFileManager.defaultManager.removeItemAtPath(newPath, null)
    } else if (!overwrite && fileExists(newPath)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "destination already exists")).withErrPath { newPath }
    }
    val success = NSFileManager.defaultManager.moveItemAtPath(oldPath, toPath = newPath, null)
    if (success) return Result.success(Unit)
    return Result.failure<Unit>(IoException(IoErrorKind.Other, "failed to move file from $oldPath to $newPath")).withErrPath { oldPath }
}

internal actual fun keepFile(path: String): Result<Unit> = Result.success(Unit)

internal actual fun readBytes(path: String): Result<ByteArray> = appleReadBytes(path)

internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> = appleWriteBytes(path, bytes)
