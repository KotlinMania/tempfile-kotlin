// port-lint: source src/util.rs
package io.github.kotlinmania.tempfile

import kotlin.random.Random

/**
 * Maximum number of times [createHelper] retries with a fresh random name
 * before giving up. Upstream `crate::NUM_RETRIES` is declared in `src/lib.rs`
 * and used only here; kept private to this file in the Kotlin port so
 * `Lib.kt` is not a Rust-shaped catchall.
 */
private const val NUM_RETRIES: Int = 65536

private val ALPHANUMERIC: CharArray = (
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "abcdefghijklmnopqrstuvwxyz" +
        "0123456789"
    ).toCharArray()

internal fun tmpname(rng: Random, prefix: String, suffix: String, randLen: Int): String {
    val sb = StringBuilder(prefix.length + suffix.length + randLen)
    sb.append(prefix)
    repeat(randLen) {
        sb.append(ALPHANUMERIC[rng.nextInt(ALPHANUMERIC.size)])
    }
    sb.append(suffix)
    return sb.toString()
}

internal fun <R> createHelper(
    base: String,
    prefix: String,
    suffix: String,
    randomLen: Int,
    f: (String) -> Result<R>,
): Result<R> {
    // Make the path absolute. Otherwise, changing the current directory can
    // invalidate a stored path (causing issues when cleaning up temporary
    // files).
    val absoluteBase = if (isAbsolutePath(base)) {
        base
    } else {
        val cwd = currentDir()
        if (cwd != null && cwd.isNotEmpty() && cwd != ".") {
            joinPath(cwd, base)
        } else {
            base
        }
    }

    val numRetries = if (randomLen != 0) NUM_RETRIES else 1

    // We fork the random generator. Kotlin's Random.Default is already
    // seeded from system randomness; upstream re-seeds from getrandom after
    // three failures, which we approximate with Random.Default (re-derived
    // per attempt at i==3) since Kotlin doesn't expose a separate forking
    // RNG.
    var rng: Random = Random.Default
    for (i in 0 until numRetries) {
        if (i == 3) rng = Random.Default
        val path = joinPath(absoluteBase, tmpname(rng, prefix, suffix, randomLen))
        val result = f(path)
        val err = result.exceptionOrNull()
        if (err is IoException && numRetries > 1) {
            // Retry on collision (AlreadyExists) or socket-path collision
            // (AddrInUse).
            if (err.kind == IoErrorKind.AlreadyExists || err.kind == IoErrorKind.AddrInUse) {
                continue
            }
        }
        return result
    }

    return Result.failure<R>(
        IoException(IoErrorKind.AlreadyExists, "too many temporary files exist"),
    ).withErrPath { absoluteBase }
}

/**
 * Returns `true` if [path] starts with a POSIX root (`/` or `\\`) or a Windows
 * drive letter (`C:/...`, `C:\...`). Mirrors the minimal subset of
 * `std::path::Path::is_absolute` the upstream `create_helper` checks.
 */
internal fun isAbsolutePath(path: String): Boolean {
    if (path.isEmpty()) return false
    if (path[0] == '/' || path[0] == '\\') return true
    if (path.length >= 3 && path[1] == ':' && (path[2] == '/' || path[2] == '\\')) {
        val drive = path[0]
        if ((drive in 'A'..'Z') || (drive in 'a'..'z')) return true
    }
    return false
}

/**
 * Joins [leaf] onto [base], inserting a separator if [base] does not end in
 * one. If [leaf] is absolute, it is returned verbatim.
 */
internal fun joinPath(base: String, leaf: String): String {
    if (leaf.isEmpty()) return base
    if (isAbsolutePath(leaf)) return leaf
    if (base.isEmpty() || base == ".") return leaf
    val last = base.last()
    return if (last == '/' || last == '\\') base + leaf else "$base/$leaf"
}
