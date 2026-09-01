// Platform actual for [systemTempDir] on JS: delegate to Node's `os.tmpdir()`.
// Routed through `eval('require')` so the webpack browser bundle does not
// try to resolve `os` at bundle time. Browsers don't have a real tempdir;
// callers that hit this code path on a browser must plug in an override
// via `overrideTempDir`.
package io.github.kotlinmania.tempfile

private fun nodeTmpdir(): String? = js(
    "(function(){ try { var r = typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : (typeof require === 'function' ? require : null); return r ? r('os').tmpdir() : null; } catch (e) { return null; } })()",
) as? String

actual fun systemTempDir(): String = nodeTmpdir() ?: "/tmp"
