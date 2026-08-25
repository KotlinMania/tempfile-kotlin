package io.github.kotlinmania.tempfile

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun systemTempDir(): String {
    val temp = getenv("TEMP")?.toKString()
    if (!temp.isNullOrEmpty()) return temp
    val tmp = getenv("TMP")?.toKString()
    if (!tmp.isNullOrEmpty()) return tmp
    val userProfile = getenv("USERPROFILE")?.toKString()
    if (!userProfile.isNullOrEmpty()) return "$userProfile\\AppData\\Local\\Temp"
    return "C:\\Temp"
}
