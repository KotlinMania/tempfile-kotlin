// port-lint: ignore
// Wasm-WASI actuals use kotlinx-io's SystemFileSystem rather than POSIX
// cinterop. SystemFileSystem.createDirectories is recursive (creates
// intermediate parents), so we mimic atomic single-mkdir by checking
// existence first and returning AlreadyExists ourselves.
package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.joinPath
import io.github.kotlinmania.tempfile.withErrPath
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

internal actual fun createTempDirAt(path: String): Result<Unit> {
    val p = Path(path)
    if (SystemFileSystem.exists(p)) {
        return Result.failure<Unit>(
            IoException(IoErrorKind.AlreadyExists, "createTempDirAt: $path already exists"),
        ).withErrPath { path }
    }
    return runCatching { SystemFileSystem.createDirectories(p) }
        .map { }
        .mapErrorToIoException(path)
}

internal actual fun removeDirAll(path: String): Result<Unit> {
    val p = Path(path)
    if (!SystemFileSystem.exists(p)) return Result.success(Unit)
    return removeRecursive(p).withErrPath { path }
}

private fun removeRecursive(p: Path): Result<Unit> {
    val meta = SystemFileSystem.metadataOrNull(p) ?: return Result.success(Unit)
    if (meta.isDirectory) {
        for (childName in SystemFileSystem.list(p)) {
            val child = Path(joinPath(p.toString(), childName.toString()))
            val r = removeRecursive(child)
            if (r.isFailure) return r
        }
    }
    return runCatching { SystemFileSystem.delete(p, mustExist = false) }
}

private fun Result<Unit>.mapErrorToIoException(path: String): Result<Unit> {
    val err = exceptionOrNull() ?: return this
    return Result.failure<Unit>(
        IoException(IoErrorKind.Other, err),
    ).withErrPath { path }
}
