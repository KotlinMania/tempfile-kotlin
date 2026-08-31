// port-lint: source file/imp/windows.rs
package io.github.kotlinmania.tempfile.file.imp

import io.github.kotlinmania.tempfile.file.NamedTempFile
import io.github.kotlinmania.tempfile.file.TempPath
import io.github.kotlinmania.tempfile.file.createNamedFile
import io.github.kotlinmania.tempfile.file.keepFile
import io.github.kotlinmania.tempfile.file.persistFile

internal fun createNamedWindows(
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

internal fun createWindows(dir: String): Result<NamedTempFile<String>> =
    NamedTempFile.newIn(dir)

internal fun reopenWindows(file: String, path: String): Result<String> =
    Result.success(path)

internal fun persistWindows(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit> =
    persistFile(oldPath, newPath, overwrite)

internal fun keepWindows(path: String): Result<Unit> =
    keepFile(path)
