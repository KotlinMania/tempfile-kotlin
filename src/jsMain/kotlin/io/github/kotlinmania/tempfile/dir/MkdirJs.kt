package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

private fun getNodeFs(): dynamic = js(
    "(function(){ try { var r = typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : (typeof require === 'function' ? require : null); return r ? r('fs') : null; } catch (e) { return null; } })()",
)

internal val inMemoryDirs = mutableSetOf<String>()
internal val inMemoryFiles = mutableMapOf<String, ByteArray>()

internal actual fun createTempDirAt(path: String): Result<Unit> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            fs.mkdirSync(path)
            Unit
        }.mapErrorToIoException(path)
    }
    if (inMemoryDirs.contains(path)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "Directory already exists: $path")).withErrPath { path }
    }
    inMemoryDirs.add(path)
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
    inMemoryDirs.remove(path)
    inMemoryDirs.removeAll { it.startsWith(path) }
    inMemoryFiles.keys.toList().forEach { k ->
        if (k.startsWith(path)) {
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
