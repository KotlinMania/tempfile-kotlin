# port-lint Proposed Changes

**Generated:** 2026-08-24
**Source:** tmp/tempfile
**Target:** src

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `commonTest/kotlin/io/github/kotlinmania/tempfile/dir/TempDirTest.kt` | `// port-lint: source src/dir/mod.rs` | `// port-lint: source dir/imp/mod.rs` | `dir/imp/mod.rs` | `port-lint provenance header matched only by basename: 'src/dir/mod.rs' vs expected 'dir/imp/mod.rs'` |
