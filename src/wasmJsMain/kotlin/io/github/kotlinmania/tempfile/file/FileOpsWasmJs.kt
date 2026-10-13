@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.dir.isWasmDirCreated
import io.github.kotlinmania.tempfile.dir.wasmInMemoryDirs
import io.github.kotlinmania.tempfile.dir.wasmInMemoryFiles
import io.github.kotlinmania.tempfile.withErrPath

private fun isNodeFsAvailable(): Boolean =
    js("typeof process !== 'undefined' && (typeof process.getBuiltinModule === 'function' || typeof globalThis.require === 'function')")

private fun nodeFsCreateFileSync(path: String) {
    js("const fs = typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('fs') : globalThis.require('fs'); const fd = fs.openSync(path, 'wx+'); fs.closeSync(fd);")
}

private fun nodeFsUnlinkSync(path: String) {
    js("(typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('fs') : globalThis.require('fs')).unlinkSync(path)")
}

private fun nodeFsExistsSync(path: String): Boolean =
    js("(typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('fs') : globalThis.require('fs')).existsSync(path)")

private fun nodeFsRenameSync(oldPath: String, newPath: String) {
    js("(typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('fs') : globalThis.require('fs')).renameSync(oldPath, newPath)")
}

private fun nodeErrorCode(e: Throwable): String? {
    val msg = e.message ?: return null
    val idx = msg.indexOf(':')
    return if (idx > 0) msg.substring(0, idx) else null
}

internal actual fun createNamedFile(path: String): Result<Unit> {
    if (isNodeFsAvailable()) {
        return runCatching { nodeFsCreateFileSync(path) }.mapErrorToIoException(path)
    }
    val parent = path.substringBeforeLast('/', "")
    if (parent.isNotEmpty() && !isWasmDirCreated(parent)) {
        return Result
            .failure<Unit>(
                IoException(IoErrorKind.NotFound, "createNamedFile: parent directory not found: $parent"),
            ).withErrPath { path }
    }
    if (wasmInMemoryFiles.containsKey(path)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "File already exists: $path")).withErrPath { path }
    }
    wasmInMemoryFiles[path] = ByteArray(0)
    return Result.success(Unit)
}

internal actual fun removeFile(path: String): Result<Unit> {
    if (isNodeFsAvailable()) {
        return runCatching { nodeFsUnlinkSync(path) }.mapErrorToIoException(path)
    }
    if (!wasmInMemoryFiles.containsKey(path)) {
        return Result.failure<Unit>(IoException(IoErrorKind.NotFound, "No such file: $path")).withErrPath { path }
    }
    wasmInMemoryFiles.remove(path)
    return Result.success(Unit)
}

internal actual fun fileExists(path: String): Boolean {
    if (path.isEmpty()) return false
    if (isNodeFsAvailable()) {
        return runCatching { nodeFsExistsSync(path) }.getOrDefault(false)
    }
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    return wasmInMemoryFiles.containsKey(path) || wasmInMemoryDirs.contains(normalized) || normalized == "/tmp"
}

internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> {
    if (isNodeFsAvailable()) {
        return runCatching {
            if (!overwrite && nodeFsExistsSync(newPath)) {
                throw Exception("EEXIST: file already exists")
            }
            nodeFsRenameSync(oldPath, newPath)
        }.mapErrorToIoException(oldPath)
    }
    if (!overwrite && wasmInMemoryFiles.containsKey(newPath)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "File already exists: $newPath")).withErrPath { oldPath }
    }
    val content =
        wasmInMemoryFiles.remove(oldPath)
            ?: return Result.failure<Unit>(IoException(IoErrorKind.NotFound, "No such file: $oldPath")).withErrPath { oldPath }
    wasmInMemoryFiles[newPath] = content
    return Result.success(Unit)
}

internal actual fun keepFile(path: String): Result<Unit> = Result.success(Unit)

internal actual fun readBytes(path: String): Result<ByteArray> {
    val data =
        wasmInMemoryFiles[path]
            ?: return Result.failure<ByteArray>(IoException(IoErrorKind.NotFound, "No such file: $path")).withErrPath { path }
    return Result.success(data)
}

internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> {
    wasmInMemoryFiles[path] = bytes
    return Result.success(Unit)
}

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
