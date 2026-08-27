// port-lint: source tempfile/src/lib.rs
package io.github.kotlinmania.tempfile

import io.github.kotlinmania.tempfile.dir.TempDir
import io.github.kotlinmania.tempfile.dir.createTempDirAt

/**
 * Default number of random characters in a temporary file/directory name.
 * Matches upstream `crate::NUM_RAND_CHARS` in `src/lib.rs`. Lives alongside
 * [Builder] (its primary caller, via `Builder::default`) rather than in a
 * Rust-shaped `Lib.kt` central catch-all.
 */
internal const val NUM_RAND_CHARS: Int = 6

/**
 * Create a new temporary file or directory with custom options.
 *
 * The defaults match upstream `Builder::default`:
 *  - `prefix = ".tmp"`
 *  - `suffix = ""`
 *  - `randomLen = NUM_RAND_CHARS` (6)
 *  - `append = false`
 *  - `disableCleanup = false`
 *
 * Permissions configuration (`Builder::permissions`) is not yet ported and
 * intentionally absent from this surface.
 */
class Builder internal constructor(
    internal var prefix: String,
    internal var suffix: String,
    internal var randomLen: Int,
    internal var append: Boolean,
    internal var disableCleanup: Boolean,
) {
    constructor() : this(
        prefix = ".tmp",
        suffix = "",
        randomLen = NUM_RAND_CHARS,
        append = false,
        disableCleanup = false,
    )

    /** Set a custom filename prefix. */
    fun prefix(prefix: String): Builder {
        this.prefix = prefix
        return this
    }

    /** Set a custom filename suffix (extension). */
    fun suffix(suffix: String): Builder {
        this.suffix = suffix
        return this
    }

    /**
     * Set the number of random characters in the filename. A value of `0`
     * makes the filename deterministic, in which case it is the caller's
     * responsibility to avoid name collisions.
     */
    fun randBytes(rand: Int): Builder {
        require(rand >= 0) { "rand must be non-negative" }
        this.randomLen = rand
        return this
    }

    /**
     * Set whether the temporary file should be opened in append mode.
     * Currently only consumed by `Builder.tempfile`/`tempfileIn` (not yet
     * ported); accepted on the Builder surface for API parity.
     */
    fun append(append: Boolean): Builder {
        this.append = append
        return this
    }

    /**
     * Set whether to disable automatic cleanup of the temporary file or
     * directory when the resulting handle goes out of scope. Defaults to
     * `false` (cleanup enabled).
     */
    fun disableCleanup(disableCleanup: Boolean): Builder {
        this.disableCleanup = disableCleanup
        return this
    }

    /**
     * Attempts to make a temporary directory inside of [tempDir]. The
     * directory and everything inside it will be automatically deleted
     * once the returned [TempDir] is closed.
     */
    fun tempdir(): Result<TempDir> = tempdirIn(tempDir())

    /**
     * Attempts to make a temporary directory inside of [dir]. The directory
     * and everything inside it will be automatically deleted once the
     * returned [TempDir] is closed.
     */
    fun tempdirIn(dir: String): Result<TempDir> =
        createHelper(dir, prefix, suffix, randomLen) { path ->
            createTempDirAt(path).map {
                TempDir.fromCreatedPath(path, disableCleanup = disableCleanup)
            }
        }
}
