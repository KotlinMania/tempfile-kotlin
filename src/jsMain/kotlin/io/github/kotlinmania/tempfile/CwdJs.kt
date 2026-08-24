package io.github.kotlinmania.tempfile

@JsModule("process")
@JsNonModule
private external object NodeProcess {
    fun cwd(): String
}

actual fun currentDir(): String? = runCatching { NodeProcess.cwd() }.getOrNull()
