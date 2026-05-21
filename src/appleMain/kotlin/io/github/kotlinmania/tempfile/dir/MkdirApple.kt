// port-lint: ignore
package io.github.kotlinmania.tempfile.dir

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import platform.posix.dirent
import platform.posix.mkdir

@OptIn(ExperimentalForeignApi::class)
internal actual fun posixMkdir(path: String): Int = mkdir(path, 0b111_111_111u)

@OptIn(ExperimentalForeignApi::class)
internal actual fun posixDirentName(entry: CPointer<*>): String? {
    val d = entry.reinterpret<dirent>().pointed
    return d.d_name.toKString()
}
