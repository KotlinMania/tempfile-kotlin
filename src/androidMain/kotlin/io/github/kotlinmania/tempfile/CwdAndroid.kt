// port-lint: ignore
// Android actual for [currentDir]. Same as JVM: read the `user.dir` system
// property the runtime populates at startup.
package io.github.kotlinmania.tempfile

actual fun currentDir(): String? = System.getProperty("user.dir")
