package io.github.kotlinmania.tempfile

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun currentDir(): String? = getenv("CD")?.toKString()
