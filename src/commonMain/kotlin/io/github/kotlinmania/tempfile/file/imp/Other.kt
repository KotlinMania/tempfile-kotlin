// port-lint: source file/imp/other.rs
package io.github.kotlinmania.tempfile.file.imp

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.file.NamedTempFile
import io.github.kotlinmania.tempfile.file.TempPath
import io.github.kotlinmania.tempfile.file.createNamedFile
import io.github.kotlinmania.tempfile.file.keepFile
import io.github.kotlinmania.tempfile.file.persistFile

private fun <T> notSupported(msg: String = "operation not supported on this platform"): Result<T> =
    Result.failure(IoException(IoErrorKind.Other, msg))

internal fun createNamedOther(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<NamedTempFile<String>> =
    createNamedFile(path).map {
        NamedTempFile(
            TempPath.new(path, disableCleanup),
            path,
        )
    }

internal fun createOther(dir: String): Result<NamedTempFile<String>> =
    NamedTempFile.newIn(dir)

internal fun reopenOther(file: String, path: String): Result<String> =
    Result.success(path)

internal fun persistOther(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> =
    persistFile(oldPath, newPath, overwrite)

internal fun keepOther(path: String): Result<Unit> =
    keepFile(path)

internal fun createNamedImp(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<NamedTempFile<String>> =
    createNamedOther(path, permissions, disableCleanup)

internal fun createImp(dir: String): Result<NamedTempFile<String>> =
    createOther(dir)

internal fun reopenImp(file: String, path: String): Result<String> =
    reopenOther(file, path)

internal fun persistImp(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> =
    persistOther(oldPath, newPath, overwrite)

internal fun keepImp(path: String): Result<Unit> =
    keepOther(path)
