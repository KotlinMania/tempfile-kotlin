// Platform actual for [systemTempDir] on Wasm-JS: delegate to Node's
// `os.tmpdir()` via interop. Browsers don't have a real tempdir.
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile

private fun nodeOsTmpdir(): String =
    js("require('os').tmpdir()")

actual fun systemTempDir(): String = nodeOsTmpdir()
