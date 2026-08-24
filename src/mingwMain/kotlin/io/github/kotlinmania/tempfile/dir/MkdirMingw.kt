package io.github.kotlinmania.tempfile.dir

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import platform.posix.mkdir

// mingw's `mkdir` is the Windows MSVCRT shim and takes only a path argument
// (no mode). Mode bits would be ignored anyway since NTFS permissions are
// ACL-based, not POSIX-mode.
@OptIn(ExperimentalForeignApi::class)
internal actual fun posixMkdir(path: String): Int = mkdir(path)

// mingw exposes `dirent` via cinterop with a `d_name` `CArrayPointer<ByteVar>`
// field. Layout is the same as POSIX; toKString() reads until the NUL.
@OptIn(ExperimentalForeignApi::class)
internal actual fun posixDirentName(entry: CPointer<*>): String? {
    val d = entry.reinterpret<platform.posix.dirent>().pointed
    return d.d_name.toKString()
}
