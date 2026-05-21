// port-lint: ignore
// Platform actual for [systemTempDir] on Wasm-WASI. WASI does not expose
// environment variables in a way Kotlin/Wasm can read at this maturity, so
// we hard-code `/tmp` and rely on the host runtime to have preopened it.
// Override via [overrideTempDir] if `/tmp` is not appropriate.
package io.github.kotlinmania.tempfile

actual fun systemTempDir(): String = "/tmp"
