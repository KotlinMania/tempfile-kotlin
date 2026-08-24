package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.joinPath
import io.github.kotlinmania.tempfile.withErrPath
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
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
import platform.posix.errno
import platform.posix.opendir
import platform.posix.readdir
import platform.posix.remove
import platform.posix.rmdir
import platform.posix.stat

@OptIn(ExperimentalForeignApi::class)
internal actual fun createTempDirAt(path: String): Result<Unit> {
    val rc = posixMkdir(path)
    if (rc == 0) return Result.success(Unit)
    return Result.failure<Unit>(
        IoException(posixErrnoToKind(errno), "mkdir failed: errno=$errno"),
    ).withErrPath { path }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun removeDirAll(path: String): Result<Unit> {
    val st = memScoped { posixLstatIsDir(path) } ?: return Result.success(Unit)
    if (!st) {
        // Not a directory; treat as a file we should unlink.
        if (remove(path) == 0) return Result.success(Unit)
        return Result.failure<Unit>(
            IoException(posixErrnoToKind(errno), "remove failed: errno=$errno"),
        ).withErrPath { path }
    }
    val dirp = opendir(path) ?: return Result.failure<Unit>(
        IoException(posixErrnoToKind(errno), "opendir failed: errno=$errno"),
    ).withErrPath { path }
    try {
        while (true) {
            val entry = readdir(dirp) ?: break
            val name = posixDirentName(entry) ?: continue
            if (name == "." || name == "..") continue
            val child = joinPath(path, name)
            val childResult = removeDirAll(child)
            if (childResult.isFailure) return childResult
        }
    } finally {
        closedir(dirp)
    }
    if (rmdir(path) == 0) return Result.success(Unit)
    return Result.failure<Unit>(
        IoException(posixErrnoToKind(errno), "rmdir failed: errno=$errno"),
    ).withErrPath { path }
}

private fun posixErrnoToKind(e: Int): IoErrorKind = when (e) {
    EEXIST -> IoErrorKind.AlreadyExists
    ENOENT -> IoErrorKind.NotFound
    EACCES -> IoErrorKind.PermissionDenied
    ENOTEMPTY -> IoErrorKind.DirectoryNotEmpty
    else -> IoErrorKind.Other
}

@OptIn(ExperimentalForeignApi::class)
internal expect fun posixMkdir(path: String): Int

@OptIn(ExperimentalForeignApi::class)
internal expect fun posixDirentName(entry: CPointer<*>): String?

@OptIn(ExperimentalForeignApi::class)
private fun posixLstatIsDir(path: String): Boolean? = memScoped {
    val sb = alloc<stat>()
    val rc = platform.posix.stat(path, sb.ptr)
    if (rc != 0) return@memScoped null
    val mode = sb.st_mode.toInt()
    (mode and S_IFDIR) != 0
}
