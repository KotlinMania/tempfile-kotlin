package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

@JsModule("fs")
@JsNonModule
private external object NodeFs {
    fun mkdirSync(path: String)
    fun rmSync(path: String, options: dynamic)
    fun existsSync(path: String): Boolean
}

internal actual fun createTempDirAt(path: String): Result<Unit> =
    runCatching { NodeFs.mkdirSync(path) }
        .mapErrorToIoException(path)

internal actual fun removeDirAll(path: String): Result<Unit> =
    runCatching {
        if (NodeFs.existsSync(path)) {
            val opts: dynamic = js("({ recursive: true, force: true })")
            NodeFs.rmSync(path, opts)
        }
    }
        .mapErrorToIoException(path)

private fun Result<Unit>.mapErrorToIoException(path: String): Result<Unit> {
    val err = exceptionOrNull() ?: return this
    val code: String = (err.asDynamic().code as? String) ?: ""
    val kind = when (code) {
        "EEXIST" -> IoErrorKind.AlreadyExists
        "ENOENT" -> IoErrorKind.NotFound
        "EACCES", "EPERM" -> IoErrorKind.PermissionDenied
        "ENOTEMPTY" -> IoErrorKind.DirectoryNotEmpty
        else -> IoErrorKind.Other
    }
    return Result.failure<Unit>(IoException(kind, err)).withErrPath { path }
}
