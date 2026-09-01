package io.github.kotlinmania.tempfile

private fun nodeCwd(): String? = js(
    "(function(){ try { var r = typeof __non_webpack_require__ !== 'undefined' ? __non_webpack_require__ : (typeof require === 'function' ? require : null); return r ? r('process').cwd() : null; } catch (e) { return null; } })()",
) as? String

actual fun currentDir(): String? = nodeCwd()
