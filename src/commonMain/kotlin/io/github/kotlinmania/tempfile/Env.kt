// port-lint: source tempfile/tests/env.rs
package io.github.kotlinmania.tempfile

import kotlin.concurrent.atomics.AtomicReference

private val DEFAULT_TEMPDIR: AtomicReference<String?> = AtomicReference(null)

/**
 * Override the default temporary directory (defaults to [systemTempDir]). This function
 * changes the *global* default temporary directory for the entire program and should not be called
 * except in exceptional cases where it's not configured correctly by the platform. Applications
 * should first check if the path returned by [tempDir] is acceptable.
 *
 * If you're writing a library and want to control where your temporary files are placed, you
 * should instead use the `In` variants of the various temporary file/directory constructors
 * ([tempdirIn], [tempfileIn], the so-named functions on [Builder], etc.).
 *
 * Only the first call to this function will succeed. All further calls will fail with a [Result]
 * carrying the previously set default temporary directory override.
 *
 * **NOTE:** This function does not check if the specified directory exists and/or is writable.
 */
fun overrideTempDir(path: String): Result<Unit> {
    if (DEFAULT_TEMPDIR.compareAndSet(null, path)) {
        return Result.success(Unit)
    }
    val existing = DEFAULT_TEMPDIR.load() ?: path
    return Result.failure(OverrideTempDirAlreadySet(existing))
}

/**
 * Returns the default temporary directory, used for both temporary directories and files if no
 * directory is explicitly specified.
 *
 * This function simply delegates to [systemTempDir] unless the default temporary directory
 * has been overridden by a call to [overrideTempDir].
 *
 * **NOTE:** This function does not check if the returned directory exists and/or is writable.
 */
fun tempDir(): String = DEFAULT_TEMPDIR.load() ?: systemTempDir()

/**
 * Thrown by [overrideTempDir] when an override was already set. Carries the previously set path
 * in [previouslySet].
 */
class OverrideTempDirAlreadySet(
    val previouslySet: String,
) : IllegalStateException("temporary directory override already set to \"$previouslySet\"")

/**
 * Returns the platform's default temporary directory.
 *
 *  - JVM / Android: `java.io.tmpdir` system property.
 *  - Apple / Linux / Windows / Android Native: `$TMPDIR` if set, else `/tmp` (Windows: `%TEMP%`,
 *    else `%TMP%`, else `%USERPROFILE%\AppData\Local\Temp`).
 *  - JS / Wasm-JS: Node `os.tmpdir()`.
 *  - Wasm-WASI: `/tmp` (no env access; WASI host should preopen `/tmp` if needed).
 */
expect fun systemTempDir(): String
