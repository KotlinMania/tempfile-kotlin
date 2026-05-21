// port-lint: ignore
// `std::env::current_dir()` equivalent. Used by `createHelper` to make a
// relative base path absolute so subsequent `chdir` calls don't invalidate
// stored temporary-file paths. Returns null if the current directory cannot
// be retrieved (e.g. permission denied, deleted from underneath, or the
// host runtime does not expose one).
package io.github.kotlinmania.tempfile

expect fun currentDir(): String?
