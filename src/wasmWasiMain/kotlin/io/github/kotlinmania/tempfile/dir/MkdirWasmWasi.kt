// Wasm-WASI actuals for directory creation and removal.
package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath

private val createdDirs: MutableSet<String> = mutableSetOf()

internal actual fun createTempDirAt(path: String): Result<Unit> {
    if (path in createdDirs) {
        return Result.failure<Unit>(
            IoException(IoErrorKind.AlreadyExists, "createTempDirAt: $path already exists"),
        ).withErrPath { path }
    }
    createdDirs.add(path)
    return Result.success(Unit)
}

internal actual fun removeDirAll(path: String): Result<Unit> {
    createdDirs.remove(path)
    val prefix = if (path.endsWith("/")) path else "$path/"
    val toRemove = createdDirs.filter { it.startsWith(prefix) }
    createdDirs.removeAll(toRemove.toSet())
    return Result.success(Unit)
}
