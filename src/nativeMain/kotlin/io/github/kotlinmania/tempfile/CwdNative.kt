// port-lint: ignore
package io.github.kotlinmania.tempfile

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.getcwd

// mingw's `getcwd` second argument is `Int`; POSIX `getcwd` takes `size_t`
// (ULong on 64-bit). `convert()` lifts the buffer-size literal to whichever
// width the target's `posix.h` declares.
@OptIn(ExperimentalForeignApi::class)
actual fun currentDir(): String? = memScoped {
    val bufSize = 4096
    val buf = allocArray<ByteVar>(bufSize)
    val ptr = getcwd(buf, bufSize.convert())
    ptr?.toKString()
}
