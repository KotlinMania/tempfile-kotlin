// port-lint: source src/lib.rs
package io.github.kotlinmania.tempfile

/**
 * Maximum number of times `Builder.tempfile`/`Builder.tempdir` and their
 * underlying [createHelper] should retry creating a temporary file or
 * directory with a fresh random name before giving up. Matches upstream
 * `NUM_RETRIES` in `src/lib.rs`.
 */
internal const val NUM_RETRIES: Int = 65536

/**
 * Default number of random characters in a temporary file/directory name.
 * Matches upstream `NUM_RAND_CHARS` in `src/lib.rs`.
 */
internal const val NUM_RAND_CHARS: Int = 6
