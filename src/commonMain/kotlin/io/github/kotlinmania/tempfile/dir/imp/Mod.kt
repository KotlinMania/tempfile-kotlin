// port-lint: source dir/imp/mod.rs
package io.github.kotlinmania.tempfile.dir.imp

import io.github.kotlinmania.tempfile.dir.TempDir

/**
 * Creates a directory at [path] with optional permissions and cleanup settings.
 */
internal fun create(
    path: String,
    permissions: Int? = null,
    disableCleanup: Boolean = false,
): Result<TempDir> =
    createDir(path, permissions, disableCleanup)
