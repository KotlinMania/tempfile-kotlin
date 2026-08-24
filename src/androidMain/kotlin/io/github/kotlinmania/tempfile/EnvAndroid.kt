// Platform actual for [systemTempDir] on Android: same as JVM — read the
// `java.io.tmpdir` system property.
package io.github.kotlinmania.tempfile

actual fun systemTempDir(): String =
    System.getProperty("java.io.tmpdir")
        ?: throw IllegalStateException("java.io.tmpdir system property is not set")
