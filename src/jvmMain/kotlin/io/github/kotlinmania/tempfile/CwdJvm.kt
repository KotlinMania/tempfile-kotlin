// port-lint: ignore
package io.github.kotlinmania.tempfile

actual fun currentDir(): String? =
    runCatching { java.io.File("").absoluteFile.path }.getOrNull()
