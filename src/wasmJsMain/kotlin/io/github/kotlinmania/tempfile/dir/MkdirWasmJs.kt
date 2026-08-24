@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

private fun nodeFsMkdirSync(path: String) {
    js("require('fs').mkdirSync(path)")
}

private fun nodeFsRmSyncRecursive(path: String) {
    js("require('fs').rmSync(path, { recursive: true, force: true })")
}

private fun nodeFsExistsSync(path: String): Boolean =
    js("require('fs').existsSync(path)")

private fun nodeErrorCode(e: Throwable): String? {
    val msg = e.message ?: return null
    // Node 'fs' errors stringify as e.g. "ENOENT: no such file or directory, mkdir '/x'".
    val idx = msg.indexOf(':')
    return if (idx > 0) msg.substring(0, idx) else null
}

internal actual fun createTempDirAt(path: String): Result<Unit> =
    runCatching { nodeFsMkdirSync(path) }
        .mapErrorToIoException(path)

internal actual fun removeDirAll(path: String): Result<Unit> =
    runCatching {
        if (nodeFsExistsSync(path)) {
            nodeFsRmSyncRecursive(path)
        }
    }
        .mapErrorToIoException(path)

private fun Result<Unit>.mapErrorToIoException(path: String): Result<Unit> {
    val err = exceptionOrNull() ?: return this
    val kind = when (nodeErrorCode(err)) {
        "EEXIST" -> IoErrorKind.AlreadyExists
        "ENOENT" -> IoErrorKind.NotFound
        "EACCES", "EPERM" -> IoErrorKind.PermissionDenied
        "ENOTEMPTY" -> IoErrorKind.DirectoryNotEmpty
        else -> IoErrorKind.Other
    }
    return Result.failure<Unit>(IoException(kind, err)).withErrPath { path }
}
