package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

private fun getNodeFs(): dynamic =
    js(
        "(function(){ try { var r = typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : (typeof require === 'function' ? require : null); return r ? r('fs') : null; } catch (e) { return null; } })()",
    )

internal val inMemoryDirs = mutableSetOf<String>()
internal val inMemoryFiles = mutableMapOf<String, ByteArray>()

internal fun isDirCreated(path: String): Boolean {
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    return normalized == "/tmp" || normalized in inMemoryDirs
}

internal actual fun createTempDirAt(path: String): Result<Unit> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            fs.mkdirSync(path)
            Unit
        }.mapErrorToIoException(path)
    }
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    val parent = normalized.substringBeforeLast('/', "")
    if (parent.isNotEmpty() && !isDirCreated(parent)) {
        return Result
            .failure<Unit>(
                IoException(IoErrorKind.NotFound, "createTempDirAt: parent directory does not exist: $parent"),
            ).withErrPath { path }
    }
    if (inMemoryDirs.contains(normalized)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "Directory already exists: $path")).withErrPath { path }
    }
    inMemoryDirs.add(normalized)
    return Result.success(Unit)
}

internal actual fun removeDirAll(path: String): Result<Unit> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            if (fs.existsSync(path) as Boolean) {
                val opts: dynamic = js("({ recursive: true, force: true })")
                fs.rmSync(path, opts)
            }
            Unit
        }.mapErrorToIoException(path)
    }
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    inMemoryDirs.remove(normalized)
    val prefix = "$normalized/"
    inMemoryDirs.removeAll { it.startsWith(prefix) }
    inMemoryFiles.keys.toList().forEach { k ->
        if (k.startsWith(prefix) || k == normalized) {
            inMemoryFiles.remove(k)
        }
    }
    return Result.success(Unit)
}

private fun Result<Unit>.mapErrorToIoException(path: String): Result<Unit> {
    val err = exceptionOrNull() ?: return this
    if (err is IoException) return this
    val code: String = (err.asDynamic().code as? String) ?: ""
    val kind =
        when (code) {
            "EEXIST" -> IoErrorKind.AlreadyExists
            "ENOENT" -> IoErrorKind.NotFound
            "EACCES", "EPERM" -> IoErrorKind.PermissionDenied
            "ENOTEMPTY" -> IoErrorKind.DirectoryNotEmpty
            else -> IoErrorKind.Other
        }
    return Result.failure<Unit>(IoException(kind, err)).withErrPath { path }
}
