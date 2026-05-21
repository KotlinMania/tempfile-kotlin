// port-lint: ignore
// Linux actual stub for the windows-only env probe: always null on POSIX.
package io.github.kotlinmania.tempfile

internal actual fun windowsTempDirFallback(): String? = null
