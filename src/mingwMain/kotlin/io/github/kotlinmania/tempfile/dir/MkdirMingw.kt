package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.EACCES
import platform.posix.EEXIST
import platform.posix.ENOENT
import platform.posix.ENOTEMPTY
import platform.posix.errno
import platform.posix.mkdir
import platform.posix.remove
import platform.posix.rmdir

@OptIn(ExperimentalForeignApi::class)
internal actual fun createTempDirAt(path: String): Result<Unit> {
    val rc = mkdir(path)
    if (rc == 0) return Result.success(Unit)
    return Result
        .failure<Unit>(
            IoException(mingwErrnoToKind(errno), "mkdir failed: errno=$errno"),
        ).withErrPath { path }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun removeDirAll(path: String): Result<Unit> {
    if (rmdir(path) == 0 || remove(path) == 0) return Result.success(Unit)
    return Result
        .failure<Unit>(
            IoException(mingwErrnoToKind(errno), "rmdir failed: errno=$errno"),
        ).withErrPath { path }
}

private fun mingwErrnoToKind(e: Int): IoErrorKind =
    when (e) {
        EEXIST -> IoErrorKind.AlreadyExists
        ENOENT -> IoErrorKind.NotFound
        EACCES -> IoErrorKind.PermissionDenied
        ENOTEMPTY -> IoErrorKind.DirectoryNotEmpty
        else -> IoErrorKind.Other
    }
