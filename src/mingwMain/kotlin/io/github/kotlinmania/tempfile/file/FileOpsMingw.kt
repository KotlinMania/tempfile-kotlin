package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import platform.posix.EACCES
import platform.posix.EEXIST
import platform.posix.ENOENT
import platform.posix.ENOTEMPTY
import platform.posix.FILE
import platform.posix.O_BINARY
import platform.posix.O_CREAT
import platform.posix.O_EXCL
import platform.posix.O_RDWR
import platform.posix.close
import platform.posix.errno
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fwrite
import platform.posix.open
import platform.posix.remove
import platform.posix.rename
import platform.posix.stat

@OptIn(ExperimentalForeignApi::class)
internal actual fun createNamedFile(path: String): Result<Unit> {
    val fd = open(path, O_RDWR or O_CREAT or O_EXCL or O_BINARY, 0b110_000_000)
    if (fd >= 0) {
        close(fd)
        return Result.success(Unit)
    }

    return Result
        .failure<Unit>(
            IoException(mingwErrnoToKind(errno), "create file failed: errno=$errno"),
        ).withErrPath { path }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun removeFile(path: String): Result<Unit> {
    if (remove(path) == 0) return Result.success(Unit)
    return Result
        .failure<Unit>(
            IoException(mingwErrnoToKind(errno), "remove file failed: errno=$errno"),
        ).withErrPath { path }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun fileExists(path: String): Boolean =
    if (path.isEmpty()) {
        false
    } else {
        memScoped {
            val sb = alloc<stat>()
            stat(path, sb.ptr) == 0
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> {
    if (overwrite) {
        remove(newPath)
        if (rename(oldPath, newPath) == 0) return Result.success(Unit)
        return Result
            .failure<Unit>(
                IoException(mingwErrnoToKind(errno), "rename failed: errno=$errno"),
            ).withErrPath { oldPath }
    } else {
        if (fileExists(newPath)) {
            return Result
                .failure<Unit>(
                    IoException(IoErrorKind.AlreadyExists, "destination path already exists"),
                ).withErrPath { newPath }
        }
        if (rename(oldPath, newPath) == 0) return Result.success(Unit)
        return Result
            .failure<Unit>(
                IoException(mingwErrnoToKind(errno), "rename failed: errno=$errno"),
            ).withErrPath { oldPath }
    }
}

internal actual fun keepFile(path: String): Result<Unit> = Result.success(Unit)

@OptIn(ExperimentalForeignApi::class)
internal actual fun readBytes(path: String): Result<ByteArray> {
    val fp: CPointer<FILE>? = fopen(path, "rb")
    if (fp == null) {
        return Result
            .failure<ByteArray>(
                IoException(mingwErrnoToKind(errno), "fopen failed: errno=$errno"),
            ).withErrPath { path }
    }
    try {
        val bytes = mutableListOf<Byte>()
        val buf = ByteArray(4096)
        while (true) {
            val readCount =
                buf.usePinned { pinned ->
                    fread(pinned.addressOf(0), 1u.convert(), 4096u.convert(), fp).toInt()
                }
            if (readCount <= 0) break
            for (i in 0 until readCount) {
                bytes.add(buf[i])
            }
        }
        return Result.success(bytes.toByteArray())
    } finally {
        fclose(fp)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> {
    val fp: CPointer<FILE>? = fopen(path, "wb")
    if (fp == null) {
        return Result
            .failure<Unit>(
                IoException(mingwErrnoToKind(errno), "fopen failed: errno=$errno"),
            ).withErrPath { path }
    }
    try {
        if (bytes.isNotEmpty()) {
            val written =
                bytes.usePinned { pinned ->
                    fwrite(pinned.addressOf(0), 1u.convert(), bytes.size.toULong().convert(), fp).toInt()
                }
            if (written < bytes.size) {
                return Result
                    .failure<Unit>(
                        IoException(IoErrorKind.Other, "failed to write all bytes"),
                    ).withErrPath { path }
            }
        }
        return Result.success(Unit)
    } finally {
        fclose(fp)
    }
}

private fun mingwErrnoToKind(e: Int): IoErrorKind =
    when (e) {
        EEXIST -> IoErrorKind.AlreadyExists
        ENOENT -> IoErrorKind.NotFound
        EACCES -> IoErrorKind.PermissionDenied
        ENOTEMPTY -> IoErrorKind.DirectoryNotEmpty
        else -> IoErrorKind.Other
    }
