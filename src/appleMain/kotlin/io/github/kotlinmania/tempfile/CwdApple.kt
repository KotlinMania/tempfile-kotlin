package io.github.kotlinmania.tempfile

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.getcwd

@OptIn(ExperimentalForeignApi::class)
actual fun currentDir(): String? =
    memScoped {
        val bufSize = 4096
        val buf = allocArray<ByteVar>(bufSize)
        val ptr = getcwd(buf, bufSize.toULong())
        ptr?.toKString()
    }
