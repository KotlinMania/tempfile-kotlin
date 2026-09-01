@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile

private fun isNodeProcessAvailable(): Boolean = js("typeof process !== 'undefined' && typeof process.cwd === 'function'")

private fun nodeProcessCwd(): String = js("process.cwd()")

actual fun currentDir(): String? = if (isNodeProcessAvailable()) runCatching { nodeProcessCwd() }.getOrNull() else "/tmp"
