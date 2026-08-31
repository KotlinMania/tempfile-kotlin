// port-lint: source tempfile/src/dir/imp/any.rs
package io.github.kotlinmania.tempfile.dir.imp

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.dir.TempDir
import io.github.kotlinmania.tempfile.dir.createTempDirAt
import io.github.kotlinmania.tempfile.withErrPath

private fun <T> notSupported(msg: String): Result<T> =
    Result.failure(IoException(IoErrorKind.Other, msg))

/**
 * Generic platform directory creation implementation.
 */
internal fun createAny(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<TempDir> =
    createTempDirAt(path)
        .withErrPath { path }
        .map { TempDir.fromCreatedPath(path, disableCleanup) }

internal fun createDir(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<TempDir> =
    createAny(path, permissions, disableCleanup)
