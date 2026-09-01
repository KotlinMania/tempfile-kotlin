// port-lint: source file/imp/unix.rs
package io.github.kotlinmania.tempfile.file.imp

import io.github.kotlinmania.tempfile.file.NamedTempFile
import io.github.kotlinmania.tempfile.file.TempPath
import io.github.kotlinmania.tempfile.file.createNamedFile
import io.github.kotlinmania.tempfile.file.keepFile
import io.github.kotlinmania.tempfile.file.persistFile

internal fun createNamedUnix(
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

internal fun createUnix(dir: String): Result<NamedTempFile<String>> =
    NamedTempFile.newIn(dir)

internal fun reopenUnix(file: String, path: String): Result<String> =
    Result.success(path)

internal fun persistUnix(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> =
    persistFile(oldPath, newPath, overwrite)

internal fun keepUnix(path: String): Result<Unit> =
    keepFile(path)
