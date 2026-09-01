@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

internal val wasmInMemoryDirs = mutableSetOf<String>()
internal val wasmInMemoryFiles = mutableMapOf<String, ByteArray>()

private fun isNodeFsAvailable(): Boolean = js("typeof require === 'function' || typeof __non_webpack_require__ === 'function'")

private fun nodeFsMkdirSync(path: String) {
    js("(typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : require)('fs').mkdirSync(path)")
}

private fun nodeFsRmSyncRecursive(path: String) {
    js("(typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : require)('fs').rmSync(path, { recursive: true, force: true })")
}

private fun nodeFsExistsSync(path: String): Boolean =
    js("(typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : require)('fs').existsSync(path)")

private fun nodeErrorCode(e: Throwable): String? {
    val msg = e.message ?: return null
    val idx = msg.indexOf(':')
    return if (idx > 0) msg.substring(0, idx) else null
}

internal actual fun createTempDirAt(path: String): Result<Unit> {
    if (isNodeFsAvailable()) {
        return runCatching { nodeFsMkdirSync(path) }.mapErrorToIoException(path)
    }
    if (wasmInMemoryDirs.contains(path)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "Directory already exists: $path")).withErrPath { path }
    }
    wasmInMemoryDirs.add(path)
    return Result.success(Unit)
}

internal actual fun removeDirAll(path: String): Result<Unit> {
    if (isNodeFsAvailable()) {
        return runCatching {
            if (nodeFsExistsSync(path)) {
                nodeFsRmSyncRecursive(path)
            }
        }.mapErrorToIoException(path)
    }
    wasmInMemoryDirs.remove(path)
    wasmInMemoryDirs.removeAll { it.startsWith(path) }
    wasmInMemoryFiles.keys.toList().forEach { k ->
        if (k.startsWith(path)) {
            wasmInMemoryFiles.remove(k)
        }
    }
    return Result.success(Unit)
}

private fun Result<Unit>.mapErrorToIoException(path: String): Result<Unit> {
    val err = exceptionOrNull() ?: return this
    val kind =
        when (nodeErrorCode(err)) {
            "EEXIST" -> IoErrorKind.AlreadyExists
            "ENOENT" -> IoErrorKind.NotFound
            "EACCES", "EPERM" -> IoErrorKind.PermissionDenied
            "ENOTEMPTY" -> IoErrorKind.DirectoryNotEmpty
            else -> IoErrorKind.Other
        }
    return Result.failure<Unit>(IoException(kind, err)).withErrPath { path }
}
