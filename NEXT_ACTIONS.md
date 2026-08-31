# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 14/14 (100.0%)
- **Function parity:** 74/111 matched (target 158) — 66.7%
- **Class/type parity:** 9/10 matched (target 16) — 90.0%
- **Combined symbol parity:** 83/121 matched (target 174) — 68.6%
- **Average inline-code cosine:** 0.27 (function body across 9 matched files)
- **Average documentation cosine:** 0.18 (doc text across 9 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 13 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. env

- **Target:** `tempfile.Env [PROVENANCE-FALLBACK]`
- **Similarity:** 0.49
- **Dependents:** 2
- **Priority Score:** 2000205.0
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/env.rs` vs expected `env.rs`
- **Proposed provenance header:** `// port-lint: source env.rs` (current: `// port-lint: source tempfile/src/env.rs`)
- **Lint issues:** 1

### 2. util

- **Target:** `tempfile.Util [PROVENANCE-FALLBACK]`
- **Similarity:** 0.63
- **Dependents:** 2
- **Priority Score:** 2000203.8
- **Functions:** 2/2 matched (target 12)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/util.rs` vs expected `util.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tempfile/src/util.rs` vs expected `util.rs`
- **Proposed provenance header:** `// port-lint: source util.rs` (current: `// port-lint: source tempfile/src/util.rs`)
- **Proposed provenance header:** `// port-lint: tests util.rs` (current: `// port-lint: tests tempfile/src/util.rs`)
- **Lint issues:** 2

### 3. error

- **Target:** `tempfile.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 0.48
- **Dependents:** 1
- **Priority Score:** 1000505.1
- **Functions:** 3/3 matched (target 10)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/error.rs` vs expected `error.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tempfile/src/error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source tempfile/src/error.rs`)
- **Proposed provenance header:** `// port-lint: tests error.rs` (current: `// port-lint: tests tempfile/src/error.rs`)
- **Lint issues:** 2

### 4. file.mod

- **Target:** `file.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 185010.0
- **Functions:** 28/45 matched (target 47)
- **Missing functions:** `from`, `deref`, `read`, `read_vectored`, `read_to_end`, `read_to_string`, `read_exact`, `write`, `flush`, `write_vectored`, `write_all`, `write_fmt`, `seek`, `as_fd`, `as_raw_fd`, `as_handle`, `as_raw_handle`
- **Types:** 4/5 matched (target 4)
- **Missing types:** `Target`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/file/mod.rs` vs expected `file/mod.rs`
- **Proposed provenance header:** `// port-lint: source file/mod.rs` (current: `// port-lint: source tempfile/src/file/mod.rs`)
- **Lint issues:** 1

### 5. imp.windows

- **Target:** `imp.Windows [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 70710.0
- **Functions:** 0/7 matched (target 5)
- **Missing functions:** `to_utf16`, `not_supported`, `create_named`, `create`, `reopen`, `keep`, `persist`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/file/imp/windows.rs` vs expected `file/imp/windows.rs`
- **Proposed provenance header:** `// port-lint: source file/imp/windows.rs` (current: `// port-lint: source tempfile/src/file/imp/windows.rs`)
- **Lint issues:** 1

### 6. file.imp.unix

- **Target:** `commonMain.kotlin.io.github.kotlinmania.tempfile.file.imp.Unix [PROVENANCE-FALLBACK]`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 60709.4
- **Functions:** 1/7 matched (target 5)
- **Missing functions:** `create_named`, `create_unlinked`, `create`, `reopen`, `persist`, `keep`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/file/imp/unix.rs` vs expected `file/imp/unix.rs`
- **Proposed provenance header:** `// port-lint: source file/imp/unix.rs` (current: `// port-lint: source tempfile/src/file/imp/unix.rs`)
- **Lint issues:** 1

### 7. imp.other

- **Target:** `imp.Other [PROVENANCE-FALLBACK]`
- **Similarity:** 0.05
- **Dependents:** 0
- **Priority Score:** 50609.5
- **Functions:** 1/6 matched (target 11)
- **Missing functions:** `create_named`, `create`, `reopen`, `persist`, `keep`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/file/imp/other.rs` vs expected `file/imp/other.rs`
- **Proposed provenance header:** `// port-lint: source file/imp/other.rs` (current: `// port-lint: source tempfile/src/file/imp/other.rs`)
- **Lint issues:** 1

### 8. imp.any

- **Target:** `imp.Any [PROVENANCE-FALLBACK]`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 10207.0
- **Functions:** 1/2 matched (target 3)
- **Missing functions:** `create`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/dir/imp/any.rs` vs expected `dir/imp/any.rs`
- **Proposed provenance header:** `// port-lint: source dir/imp/any.rs` (current: `// port-lint: source tempfile/src/dir/imp/any.rs`)
- **Lint issues:** 1

### 9. imp.unix

- **Target:** `imp.Unix [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched
- **Missing functions:** `create`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/dir/imp/unix.rs` vs expected `dir/imp/unix.rs`
- **Proposed provenance header:** `// port-lint: source dir/imp/unix.rs` (current: `// port-lint: source tempfile/src/dir/imp/unix.rs`)
- **Lint issues:** 1

### 10. spooled

- **Target:** `tempfile.Spooled [PROVENANCE-FALLBACK]`
- **Similarity:** 0.42
- **Dependents:** 0
- **Priority Score:** 2105.8
- **Functions:** 19/19 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/spooled.rs` vs expected `spooled.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tempfile/src/spooled.rs` vs expected `spooled.rs`
- **Proposed provenance header:** `// port-lint: source spooled.rs` (current: `// port-lint: source tempfile/src/spooled.rs`)
- **Proposed provenance header:** `// port-lint: tests spooled.rs` (current: `// port-lint: tests tempfile/src/spooled.rs`)
- **Lint issues:** 2

### 11. dir.mod

- **Target:** `dir.TempDir [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1810.0
- **Functions:** 17/17 matched (target 31)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/dir/mod.rs` vs expected `dir/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tempfile/src/dir/mod.rs` vs expected `dir/mod.rs`
- **Proposed provenance header:** `// port-lint: source dir/mod.rs` (current: `// port-lint: source tempfile/src/dir/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests dir/mod.rs` (current: `// port-lint: tests tempfile/src/dir/mod.rs`)
- **Lint issues:** 2

### 12. imp.mod

- **Target:** `imp.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 1)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/dir/imp/mod.rs` vs expected `dir/imp/mod.rs`
- **Proposed provenance header:** `// port-lint: source dir/imp/mod.rs` (current: `// port-lint: source tempfile/src/dir/imp/mod.rs`)
- **Lint issues:** 1

### 13. file.imp.mod

- **Target:** `commonMain.kotlin.io.github.kotlinmania.tempfile.file.imp.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 5)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tempfile/src/file/imp/mod.rs` vs expected `file/imp/mod.rs`
- **Proposed provenance header:** `// port-lint: source file/imp/mod.rs` (current: `// port-lint: source tempfile/src/file/imp/mod.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Matched

| Source | Target | Path |
|--------|--------|------|
| `lib` | `tempfile.Lib` | `lib` |

