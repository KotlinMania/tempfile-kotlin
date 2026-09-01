package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

@JsModule("fs")
@JsNonModule
private external object NodeFs {
    fun closeSync(fd: Int)

    fun openSync(path: String, flags: String): Int

    fun unlinkSync(path: String)

    fun existsSync(path: String): Boolean

    fun renameSync(oldPath: String, newPath: String)

    fun readFileSync(path: String): dynamic

    fun writeFileSync(path: String, data: dynamic)
}

internal actual fun createNamedFile(path: String): Result<Unit> =
    runCatching {
        val fd = NodeFs.openSync(path, "wx+")
        NodeFs.closeSync(fd)
    }.map { }.mapErrorToIoException(path)

internal actual fun removeFile(path: String): Result<Unit> =
    runCatching {
        NodeFs.unlinkSync(path)
    }.map { }.mapErrorToIoException(path)

internal actual fun fileExists(path: String): Boolean =
    if (path.isEmpty()) false else runCatching { NodeFs.existsSync(path) }.getOrDefault(false)

internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> =
    runCatching {
        if (!overwrite && NodeFs.existsSync(newPath)) {
            val err: dynamic = js("new Error('EEXIST')")
            err.code = "EEXIST"
            throw err as Throwable
        }
        NodeFs.renameSync(oldPath, newPath)
    }.map { }.mapErrorToIoException(oldPath)

internal actual fun keepFile(path: String): Result<Unit> = Result.success(Unit)

internal actual fun readBytes(path: String): Result<ByteArray> =
    runCatching {
        val buffer = NodeFs.readFileSync(path)
        val len = (buffer.length as? Int) ?: 0
        val bytes = ByteArray(len)
        for (i in 0 until len) {
            bytes[i] = (buffer[i] as Number).toByte()
        }
        bytes
    }.mapErrorToIoException(path)

internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> =
    runCatching {
        val nodeBuffer: dynamic = js("Buffer.from(bytes)")
        NodeFs.writeFileSync(path, nodeBuffer)
    }.map { }.mapErrorToIoException(path)

private fun <T> Result<T>.mapErrorToIoException(path: String): Result<T> {
    val err = exceptionOrNull() ?: return this
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
