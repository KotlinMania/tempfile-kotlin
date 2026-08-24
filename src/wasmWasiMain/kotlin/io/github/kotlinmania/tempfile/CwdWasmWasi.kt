package io.github.kotlinmania.tempfile

// In standard WASI preview 1, the root preopened directory is typically mapped to ".".
actual fun currentDir(): String? = "."
