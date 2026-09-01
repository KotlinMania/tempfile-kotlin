package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.dir.inMemoryDirs
import io.github.kotlinmania.tempfile.dir.inMemoryFiles
import io.github.kotlinmania.tempfile.withErrPath

private fun getNodeFs(): dynamic = js(
    "(function(){ try { var r = typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : (typeof require === 'function' ? require : null); return r ? r('fs') : null; } catch (e) { return null; } })()",
)

internal actual fun createNamedFile(path: String): Result<Unit> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            val fd = fs.openSync(path, "wx+") as Int
            fs.closeSync(fd)
            Unit
        }.mapErrorToIoException(path)
    }
    if (inMemoryFiles.containsKey(path)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "File already exists: $path")).withErrPath { path }
    }
    inMemoryFiles[path] = ByteArray(0)
    return Result.success(Unit)
}

internal actual fun removeFile(path: String): Result<Unit> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            fs.unlinkSync(path)
            Unit
        }.mapErrorToIoException(path)
    }
    if (!inMemoryFiles.containsKey(path)) {
        return Result.failure<Unit>(IoException(IoErrorKind.NotFound, "No such file: $path")).withErrPath { path }
    }
    inMemoryFiles.remove(path)
    return Result.success(Unit)
}

internal actual fun fileExists(path: String): Boolean {
    if (path.isEmpty()) return false
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching { fs.existsSync(path) as Boolean }.getOrDefault(false)
    }
    return inMemoryFiles.containsKey(path) || inMemoryDirs.contains(path)
}

internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            if (!overwrite && fs.existsSync(newPath) as Boolean) {
                val err: dynamic = js("new Error('EEXIST')")
                err.code = "EEXIST"
                throw err as Throwable
            }
            fs.renameSync(oldPath, newPath)
            Unit
        }.mapErrorToIoException(oldPath)
    }
    if (!overwrite && inMemoryFiles.containsKey(newPath)) {
        return Result.failure<Unit>(IoException(IoErrorKind.AlreadyExists, "File already exists: $newPath")).withErrPath { oldPath }
    }
    val content = inMemoryFiles.remove(oldPath)
        ?: return Result.failure<Unit>(IoException(IoErrorKind.NotFound, "No such file: $oldPath")).withErrPath { oldPath }
    inMemoryFiles[newPath] = content
    return Result.success(Unit)
}

internal actual fun keepFile(path: String): Result<Unit> = Result.success(Unit)

internal actual fun readBytes(path: String): Result<ByteArray> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            val buffer = fs.readFileSync(path)
            val len = (buffer.length as? Int) ?: 0
            val bytes = ByteArray(len)
            for (i in 0 until len) {
                bytes[i] = (buffer[i] as Number).toByte()
            }
            bytes
        }.mapErrorToIoException(path)
    }
    val data = inMemoryFiles[path]
        ?: return Result.failure<ByteArray>(IoException(IoErrorKind.NotFound, "No such file: $path")).withErrPath { path }
    return Result.success(data)
}

internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> {
    val fs = getNodeFs()
    if (fs != null) {
        return runCatching {
            val nodeBuffer: dynamic = js("Buffer.from(bytes)")
            fs.writeFileSync(path, nodeBuffer)
            Unit
        }.mapErrorToIoException(path)
    }
    inMemoryFiles[path] = bytes
    return Result.success(Unit)
}

private fun <T> Result<T>.mapErrorToIoException(path: String): Result<T> {
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
    return Result.failure<T>(IoException(kind, err)).withErrPath { path }
}
