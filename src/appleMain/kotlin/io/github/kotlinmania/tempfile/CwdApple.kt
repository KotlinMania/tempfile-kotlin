package io.github.kotlinmania.tempfile

import platform.Foundation.NSFileManager

actual fun currentDir(): String? =
    NSFileManager.defaultManager.currentDirectoryPath
