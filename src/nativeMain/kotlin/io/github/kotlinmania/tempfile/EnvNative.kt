// Platform actual for [systemTempDir] on native (Apple / Linux / mingw /
// androidNative). On POSIX-like systems we read `$TMPDIR` and fall back to
// `/tmp`. On Windows (mingw) we read `%TEMP%`, then `%TMP%`, then fall back
// to `%USERPROFILE%\AppData\Local\Temp`.
package io.github.kotlinmania.tempfile

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun systemTempDir(): String {
    val tmpdir = getenv("TMPDIR")?.toKString()
    if (!tmpdir.isNullOrEmpty()) return tmpdir
    return windowsTempDirFallback() ?: "/tmp"
}

@OptIn(ExperimentalForeignApi::class)
internal expect fun windowsTempDirFallback(): String?
