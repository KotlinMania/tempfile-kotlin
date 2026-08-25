package io.github.kotlinmania.tempfile

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun systemTempDir(): String {
    val tmpdir = getenv("TMPDIR")?.toKString()
    if (!tmpdir.isNullOrEmpty()) return tmpdir
    return "/tmp"
}
