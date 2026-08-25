# port-lint Proposed Changes

**Generated:** 2026-08-25
**Source:** tmp/tempfile/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/tempfile

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/tempfile/Util.kt` | `// port-lint: source src/util.rs` | `// port-lint: source util.rs` | `util.rs` | `port-lint provenance header matched only after fallback normalization: 'src/util.rs' vs expected 'util.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/tempfile/UtilTest.kt` | `// port-lint: source src/util.rs` | `// port-lint: source util.rs` | `util.rs` | `port-lint provenance header matched only after fallback normalization: 'src/util.rs' vs expected 'util.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/tempfile/Env.kt` | `// port-lint: source src/env.rs` | `// port-lint: source env.rs` | `env.rs` | `port-lint provenance header matched only after fallback normalization: 'src/env.rs' vs expected 'env.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/tempfile/Error.kt` | `// port-lint: source src/error.rs` | `// port-lint: source error.rs` | `error.rs` | `port-lint provenance header matched only after fallback normalization: 'src/error.rs' vs expected 'error.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/tempfile/Spooled.kt` | `// port-lint: source src/spooled.rs` | `// port-lint: source spooled.rs` | `spooled.rs` | `port-lint provenance header matched only after fallback normalization: 'src/spooled.rs' vs expected 'spooled.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/tempfile/SpooledTest.kt` | `// port-lint: tests tests/spooled.rs` | `// port-lint: tests spooled.rs` | `spooled.rs` | `port-lint provenance header matched only by basename: 'tests:tests/spooled.rs' vs expected 'spooled.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/tempfile/Lib.kt` | `// port-lint: source src/lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'src/lib.rs' vs expected 'lib.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/tempfile/Builder.kt` | `// port-lint: source src/lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'src/lib.rs' vs expected 'lib.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/tempfile/dir/TempDir.kt` | `// port-lint: source src/dir/mod.rs` | `// port-lint: source dir/mod.rs` | `dir/mod.rs` | `port-lint provenance header matched only after fallback normalization: 'src/dir/mod.rs' vs expected 'dir/mod.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/tempfile/dir/TempDirTest.kt` | `// port-lint: source src/dir/mod.rs` | `// port-lint: source dir/imp/mod.rs` | `dir/imp/mod.rs` | `port-lint provenance header matched only by basename: 'src/dir/mod.rs' vs expected 'dir/imp/mod.rs'` |
