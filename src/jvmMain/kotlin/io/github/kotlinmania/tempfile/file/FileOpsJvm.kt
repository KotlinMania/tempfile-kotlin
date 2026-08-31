package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.moveTo
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

internal actual fun createNamedFile(path: String): Result<Unit> =
    runCatching { Path(path).createFile() }
        .map { }
        .mapErrorToIoException(path)

internal actual fun removeFile(path: String): Result<Unit> =
    runCatching { Path(path).deleteExisting() }
        .map { }
        .mapErrorToIoException(path)

internal actual fun fileExists(path: String): Boolean =
    if (path.isEmpty()) false else runCatching { Path(path).exists() }.getOrDefault(false)


internal actual fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> =
    runCatching {
        val src = Path(oldPath)
        val dst = Path(newPath)
        if (overwrite) {
            src.moveTo(dst, StandardCopyOption.REPLACE_EXISTING)
        } else {
            src.moveTo(dst)
        }
    }.map { }
        .mapErrorToIoException(oldPath)

internal actual fun keepFile(path: String): Result<Unit> =
    Result.success(Unit)

internal actual fun readBytes(path: String): Result<ByteArray> =
    runCatching { Path(path).readBytes() }
        .mapErrorToIoException(path)

internal actual fun writeBytes(path: String, bytes: ByteArray): Result<Unit> =
    runCatching { Path(path).writeBytes(bytes) }
        .map { }
        .mapErrorToIoException(path)

private fun <T> Result<T>.mapErrorToIoException(path: String): Result<T> {
    val err = exceptionOrNull() ?: return this
    val kind =
        when (err::class.simpleName) {
            "FileAlreadyExistsException" -> IoErrorKind.AlreadyExists
            "NoSuchFileException" -> IoErrorKind.NotFound
            "AccessDeniedException" -> IoErrorKind.PermissionDenied
            "DirectoryNotEmptyException" -> IoErrorKind.DirectoryNotEmpty
            else -> IoErrorKind.Other
        }
    return Result.failure<T>(IoException(kind, err)).withErrPath { path }
}
