package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.joinPath
import io.github.kotlinmania.tempfile.withErrPath
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.posix.EACCES
import platform.posix.EEXIST
import platform.posix.ENOENT
import platform.posix.ENOTEMPTY
import platform.posix.S_IFDIR
import platform.posix.closedir
import platform.posix.dirent
import platform.posix.errno
import platform.posix.mkdir
import platform.posix.opendir
import platform.posix.readdir
import platform.posix.remove
import platform.posix.rmdir
import platform.posix.stat

@OptIn(ExperimentalForeignApi::class)
internal actual fun createTempDirAt(path: String): Result<Unit> {
    val rc = mkdir(path, 0b111_111_111u)
    if (rc == 0) return Result.success(Unit)
    return Result
        .failure<Unit>(
            IoException(posixErrnoToKind(errno), "mkdir failed: errno=$errno"),
        ).withErrPath { path }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun removeDirAll(path: String): Result<Unit> {
    val st = memScoped { posixLstatIsDir(path) } ?: return Result.success(Unit)
    if (!st) {
        if (remove(path) == 0) return Result.success(Unit)
        return Result
            .failure<Unit>(
                IoException(posixErrnoToKind(errno), "remove failed: errno=$errno"),
            ).withErrPath { path }
    }
    val dirp =
        opendir(path) ?: return Result
            .failure<Unit>(
                IoException(posixErrnoToKind(errno), "opendir failed: errno=$errno"),
            ).withErrPath { path }
    try {
        while (true) {
            val entry: CPointer<dirent> = readdir(dirp) ?: break
            val name = entry.pointed.d_name.toKString()
            if (name == "." || name == "..") continue
            val child = joinPath(path, name)
            val childResult = removeDirAll(child)
            if (childResult.isFailure) return childResult
        }
    } finally {
        closedir(dirp)
    }
    if (rmdir(path) == 0) return Result.success(Unit)
    return Result
        .failure<Unit>(
            IoException(posixErrnoToKind(errno), "rmdir failed: errno=$errno"),
        ).withErrPath { path }
}

private fun posixErrnoToKind(e: Int): IoErrorKind =
    when (e) {
        EEXIST -> IoErrorKind.AlreadyExists
        ENOENT -> IoErrorKind.NotFound
        EACCES -> IoErrorKind.PermissionDenied
        ENOTEMPTY -> IoErrorKind.DirectoryNotEmpty
        else -> IoErrorKind.Other
    }

@OptIn(ExperimentalForeignApi::class)
private fun posixLstatIsDir(path: String): Boolean? =
    memScoped {
        val sb = alloc<stat>()
        val rc = stat(path, sb.ptr)
        if (rc != 0) return@memScoped null
        val mode = sb.st_mode.toInt()
        (mode and S_IFDIR) != 0
    }
