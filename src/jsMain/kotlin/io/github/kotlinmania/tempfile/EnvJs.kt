// port-lint: ignore
// Platform actual for [systemTempDir] on JS: delegate to Node's `os.tmpdir()`.
// Routed through `eval('require')` so the webpack browser bundle does not
// try to resolve `os` at bundle time. Browsers don't have a real tempdir;
// callers that hit this code path on a browser must plug in an override
// via `overrideTempDir`.
package io.github.kotlinmania.tempfile

@JsModule("os")
@JsNonModule
private external object NodeOs {
    fun tmpdir(): String
}

actual fun systemTempDir(): String = NodeOs.tmpdir()
