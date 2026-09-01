// Platform actual for [systemTempDir] on Wasm-JS: delegate to Node's
// `os.tmpdir()` via interop when available, or fallback to /tmp.
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile

private fun isNodeAvailable(): Boolean = js("typeof require === 'function' || typeof __non_webpack_require__ === 'function'")

private fun nodeOsTmpdir(): String =
    js("(typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : require)('os').tmpdir()")

actual fun systemTempDir(): String = if (isNodeAvailable()) nodeOsTmpdir() else "/tmp"
