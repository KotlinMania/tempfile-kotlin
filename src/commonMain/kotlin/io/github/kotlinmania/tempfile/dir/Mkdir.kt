// Atomic single-directory creation. Used by `Builder.tempdirIn` via
// `createHelper`'s retry loop. Returns `IoErrorKind.AlreadyExists` when the
// path exists so `createHelper` triggers a re-randomize; any other failure
// is surfaced verbatim.
//
// Upstream Rust dispatches through `dir/imp/{unix,any}.rs`; the Kotlin port
// uses one `expect` per filesystem syscall and platform `actual`s for
// JVM/Android (java.nio.file.Files via System.* — no java import), Apple /
// Linux / mingw / androidNative (posix `mkdir`), JS / Wasm-JS (Node `fs`),
// and Wasm-WASI (kotlinx-io SystemFileSystem).
package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.IoException

internal expect fun createTempDirAt(path: String): Result<Unit>

/**
 * Recursively remove a directory and all its contents. Used by
 * `TempDir.close()` and the implicit cleanup path. Failure to remove is
 * surfaced as an [IoException].
 */
internal expect fun removeDirAll(path: String): Result<Unit>
