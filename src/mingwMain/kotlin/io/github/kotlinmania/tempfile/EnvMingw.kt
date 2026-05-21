// port-lint: ignore
// Windows actual for the temp dir env probe.
package io.github.kotlinmania.tempfile

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
internal actual fun windowsTempDirFallback(): String? {
    val temp = getenv("TEMP")?.toKString()
    if (!temp.isNullOrEmpty()) return temp
    val tmp = getenv("TMP")?.toKString()
    if (!tmp.isNullOrEmpty()) return tmp
    val profile = getenv("USERPROFILE")?.toKString()
    if (!profile.isNullOrEmpty()) return "$profile\\AppData\\Local\\Temp"
    return null
}
