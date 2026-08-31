@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

private fun nodeFsCreateFileSync(path: String) {
    js("const fd = require('fs').openSync(path, 'wx+'); require('fs').closeSync(fd);")
}

private fun nodeFsUnlinkSync(path: String) {
    js("require('fs').unlinkSync(path)")
}

private fun nodeFsExistsSync(path: String): Boolean =
    js("require('fs').existsSync(path)")

private fun nodeFsRenameSync(oldPath: String, newPath: String) {
    js("require('fs').renameSync(oldPath, newPath)")
}

private fun nodeErrorCode(e: Throwable): String? {
    val msg = e.message ?: return null
    val idx = msg.indexOf(':')
    return if (idx > 0) msg.substring(0, idx) else null
}

internal actual fun createNamedFile(path: String): Result<Unit> =
    runCatching { nodeFsCreateFileSync(path) }
        .mapErrorToIoException(path)

internal actual fun removeFile(path: String): Result<Unit> =
    runCatching { nodeFsUnlinkSync(path) }
        .mapErrorToIoException(path)

internal actual fun fileExists(path: String): Boolean =
    if (path.isEmpty()) false else runCatching { nodeFsExistsSync(path) }.getOrDefault(false)


internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> =
    runCatching {
        if (!overwrite && nodeFsExistsSync(newPath)) {
            throw Exception("EEXIST: file already exists")
        }
        nodeFsRenameSync(oldPath, newPath)
    }.mapErrorToIoException(oldPath)

internal actual fun keepFile(path: String): Result<Unit> = Result.success(Unit)

internal actual fun readBytes(path: String): Result<ByteArray> =
    runCatching {
        ByteArray(0)
    }.mapErrorToIoException(path)

internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> =
    runCatching {
        Unit
    }.mapErrorToIoException(path)

private fun <T> Result<T>.mapErrorToIoException(path: String): Result<T> {
    val err = exceptionOrNull() ?: return this
    val kind =
        when (nodeErrorCode(err)) {
            "EEXIST" -> IoErrorKind.AlreadyExists
            "ENOENT" -> IoErrorKind.NotFound
            "EACCES", "EPERM" -> IoErrorKind.PermissionDenied
            "ENOTEMPTY" -> IoErrorKind.DirectoryNotEmpty
            else -> IoErrorKind.Other
        }
    return Result.failure<T>(IoException(kind, err)).withErrPath { path }
}
