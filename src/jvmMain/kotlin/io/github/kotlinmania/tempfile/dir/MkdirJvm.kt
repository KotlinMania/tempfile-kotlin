package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists

internal actual fun createTempDirAt(path: String): Result<Unit> =
    runCatching { Path(path).createDirectory() }
        .map { }
        .mapErrorToIoException(path)

internal actual fun removeDirAll(path: String): Result<Unit> =
    runCatching {
        val p = Path(path)
        if (p.exists()) {
            @OptIn(kotlin.io.path.ExperimentalPathApi::class)
            p.deleteRecursively()
        }
    }.map { }
        .mapErrorToIoException(path)

private fun Result<Unit>.mapErrorToIoException(path: String): Result<Unit> {
    val err = exceptionOrNull() ?: return this
    val kind =
        when (err::class.simpleName) {
            "FileAlreadyExistsException" -> IoErrorKind.AlreadyExists
            "NoSuchFileException" -> IoErrorKind.NotFound
            "AccessDeniedException" -> IoErrorKind.PermissionDenied
            "DirectoryNotEmptyException" -> IoErrorKind.DirectoryNotEmpty
            else -> IoErrorKind.Other
        }
    return Result.failure<Unit>(IoException(kind, err)).withErrPath { path }
}
