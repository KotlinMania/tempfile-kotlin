// port-lint: ignore
// Platform actual for [systemTempDir] on JVM: read the `java.io.tmpdir` system
// property, which the JVM populates from the operating system's documented
// temp directory at startup.
package io.github.kotlinmania.tempfile

actual fun systemTempDir(): String =
    System.getProperty("java.io.tmpdir")
        ?: throw IllegalStateException("java.io.tmpdir system property is not set")
