package io.github.kotlinmania.tempfile.file

internal expect fun createNamedFile(path: String): Result<Unit>

internal expect fun removeFile(path: String): Result<Unit>

internal expect fun fileExists(path: String): Boolean

internal expect fun persistFile(oldPath: String, newPath: String, overwrite: Boolean): Result<Unit>

internal expect fun keepFile(path: String): Result<Unit>

internal expect fun readBytes(path: String): Result<ByteArray>

internal expect fun writeBytes(path: String, bytes: ByteArray): Result<Unit>
