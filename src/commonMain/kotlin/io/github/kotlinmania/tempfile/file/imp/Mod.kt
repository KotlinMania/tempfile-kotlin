// port-lint: source file/imp/mod.rs
package io.github.kotlinmania.tempfile.file.imp

import io.github.kotlinmania.tempfile.file.NamedTempFile

/**
 * Platform abstraction module matching upstream `file::imp`.
 */
internal fun createNamed(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<NamedTempFile<String>> =
    createNamedImp(path, permissions, disableCleanup)

internal fun create(
    dir: String,
): Result<NamedTempFile<String>> =
    createImp(dir)

internal fun reopen(
    file: String,
    path: String,
): Result<String> =
    reopenImp(file, path)

internal fun persist(
    oldPath: String,
    newPath: String,
    overwrite: Boolean,
): Result<Unit> =
    persistImp(oldPath, newPath, overwrite)

internal fun keep(
    path: String,
): Result<Unit> =
    keepImp(path)
