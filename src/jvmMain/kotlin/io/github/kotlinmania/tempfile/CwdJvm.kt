// port-lint: ignore
// JVM actual for [currentDir]. The JVM sets `user.dir` at startup from
// `getcwd(3)`; reading it back is Kotlin-native (no java.io import).
package io.github.kotlinmania.tempfile

actual fun currentDir(): String? = System.getProperty("user.dir")
