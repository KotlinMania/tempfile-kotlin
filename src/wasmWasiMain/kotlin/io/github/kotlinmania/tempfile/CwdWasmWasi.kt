// port-lint: ignore
package io.github.kotlinmania.tempfile

// WASI does not expose a current working directory the way POSIX does;
// `tempfile`'s only consumer of this value is the relative-to-absolute
// step in `createHelper`, which is a defensive optimization rather than
// a correctness requirement. Returning `null` causes that step to fail
// fast with NotFound rather than silently using a wrong path.
actual fun currentDir(): String? = null
