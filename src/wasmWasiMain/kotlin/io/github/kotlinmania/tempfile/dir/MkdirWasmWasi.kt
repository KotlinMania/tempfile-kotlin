// Wasm-WASI actuals for directory creation and removal.
package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.file.removeFilesUnder
import io.github.kotlinmania.tempfile.withErrPath

private val createdDirs: MutableSet<String> = mutableSetOf("/tmp", "/")

internal fun isDirCreated(path: String): Boolean {
    if (path.isEmpty()) return false
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    return normalized in createdDirs
}

internal actual fun createTempDirAt(path: String): Result<Unit> {
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    val parent = normalized.substringBeforeLast('/', "")
    if (parent.isNotEmpty() && !isDirCreated(parent)) {
        return Result
            .failure<Unit>(
                IoException(IoErrorKind.NotFound, "createTempDirAt: parent directory does not exist: $parent"),
            ).withErrPath { path }
    }
    if (normalized in createdDirs) {
        return Result
            .failure<Unit>(
                IoException(IoErrorKind.AlreadyExists, "createTempDirAt: $path already exists"),
            ).withErrPath { path }
    }
    createdDirs.add(normalized)
    return Result.success(Unit)
}

internal actual fun removeDirAll(path: String): Result<Unit> {
    val normalized = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    createdDirs.remove(normalized)
    val prefix = "$normalized/"
    val toRemove = createdDirs.filter { it.startsWith(prefix) }
    createdDirs.removeAll(toRemove.toSet())
    removeFilesUnder(prefix)
    return Result.success(Unit)
}
