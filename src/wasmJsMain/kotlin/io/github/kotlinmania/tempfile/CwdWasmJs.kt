// port-lint: ignore
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile

private fun nodeProcessCwd(): String = js("process.cwd()")

actual fun currentDir(): String? = runCatching { nodeProcessCwd() }.getOrNull()
