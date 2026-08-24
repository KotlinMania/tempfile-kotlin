// Platform actual for [systemTempDir] on Wasm-WASI. In standard WASI preview 1
// (e.g. Node.js WASI runtime), only preopened directory descriptors are accessible.
// Since the root filesystem / is typically not preopened, we use the preopened
// working directory "." as the default base directory.
package io.github.kotlinmania.tempfile

actual fun systemTempDir(): String = "."
