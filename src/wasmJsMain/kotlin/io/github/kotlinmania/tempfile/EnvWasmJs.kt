// Platform actual for [systemTempDir] on Wasm-JS: delegate to Node's
// `os.tmpdir()` via interop when available, or fallback to /tmp.
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.tempfile

private fun isNodeAvailable(): Boolean =
    js("typeof process !== 'undefined' && (typeof process.getBuiltinModule === 'function' || typeof globalThis.require === 'function')")

private fun nodeOsTmpdir(): String =
    js("(typeof process !== 'undefined' && typeof process.getBuiltinModule === 'function' ? process.getBuiltinModule('os') : globalThis.require('os')).tmpdir()")

actual fun systemTempDir(): String = if (isNodeAvailable()) nodeOsTmpdir() else "/tmp"
