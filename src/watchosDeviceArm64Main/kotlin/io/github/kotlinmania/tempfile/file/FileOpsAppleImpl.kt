package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.IoErrorKind
import io.github.kotlinmania.tempfile.IoException
import io.github.kotlinmania.tempfile.withErrPath
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual fun appleReadBytes(path: String): Result<ByteArray> =
    runCatching {
        val data = NSData.dataWithContentsOfFile(path) ?: throw IoException(IoErrorKind.NotFound, "file not found: $path")
        val len = data.length.toInt()
        val bytes = ByteArray(len)
        if (len > 0) {
            bytes.usePinned { pinned ->
                platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length.convert())
            }
        }
        bytes
    }.withErrPath { path }

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual fun appleWriteBytes(path: String, bytes: ByteArray): Result<Unit> =
    runCatching {
        val data =
            if (bytes.isEmpty()) {
                NSData()
            } else {
                bytes.usePinned { pinned ->
                    NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong().convert())
                }
            }
        val success = data.writeToFile(path, atomically = true)
        if (!success) {
            throw IoException(IoErrorKind.Other, "failed to write data to $path")
        }
    }.withErrPath { path }
