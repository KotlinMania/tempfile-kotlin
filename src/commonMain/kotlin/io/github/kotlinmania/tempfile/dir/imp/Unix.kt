// port-lint: source tempfile/src/dir/imp/unix.rs
package io.github.kotlinmania.tempfile.dir.imp

import io.github.kotlinmania.tempfile.dir.TempDir
import io.github.kotlinmania.tempfile.dir.createTempDirAt
import io.github.kotlinmania.tempfile.withErrPath

/**
 * Unix-specific directory creation implementation.
 */
internal fun createUnix(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<TempDir> =
    createTempDirAt(path)
        .withErrPath { path }
        .map { TempDir.fromCreatedPath(path, disableCleanup) }
