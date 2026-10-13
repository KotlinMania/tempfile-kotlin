@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

internal val wasmInMemoryDirs = mutableSetOf<String>()
internal val wasmInMemoryFiles = mutableMapOf<String, ByteArray>()

internal fun isWasmDirCreated(path: String): Boolean {
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    return normalized == "/tmp" || normalized in wasmInMemoryDirs
}

private fun isNodeFsAvailable(): Boolean =
    js("typeof process !== 'undefined' && (typeof process.getBuiltinModule === 'function' || typeof globalThis.require === 'function')")

private fun nodeFsMkdirSync(path: String) {
    js("(typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('fs') : globalThis.require('fs')).mkdirSync(path)")
}

private fun nodeFsRmSyncRecursive(path: String) {
    js("(typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('fs') : globalThis.require('fs')).rmSync(path, { recursive: true, force: true })")
}

private fun nodeFsExistsSync(path: String): Boolean =
    js("(typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('fs') : globalThis.require('fs')).existsSync(path)")

private fun nodeErrorCode(e: Throwable): String? {
    val msg = e.message ?: return null
    val idx = msg.indexOf(':')
    return if (idx > 0) msg.substring(0, idx) else null
}

internal actual fun createTempDirAt(path: String): Result<Unit> {
    if (isNodeFsAvailable()) {
        return runCatching { nodeFsMkdirSync(path) }.mapErrorToIoException(path)
    }
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    val parent = normalized.substringBeforeLast('/', "")
    if (parent.isNotEmpty() && !isWasmDirCreated(parent)) {
        return Result
            .failure<Unit>(
                IoException(IoErrorKind.NotFound, "createTempDirAt: parent directory does not exist: $parent"),
            ).withErrPath { path }
    }
    if (wasmInMemoryDirs.contains(normalized)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "Directory already exists: $path")).withErrPath { path }
    }
    wasmInMemoryDirs.add(normalized)
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
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    wasmInMemoryDirs.remove(normalized)
    val prefix = "$normalized/"
    wasmInMemoryDirs.removeAll { it.startsWith(prefix) }
    wasmInMemoryFiles.keys.toList().forEach { k ->
        if (k.startsWith(prefix) || k == normalized) {
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
