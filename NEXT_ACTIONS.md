# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 7/14 (50.0%)
- **Function parity:** 31/82 matched (target 60) — 37.8%
- **Class/type parity:** 5/6 matched (target 12) — 83.3%
- **Combined symbol parity:** 36/88 matched (target 72) — 40.9%
- **Average inline-code cosine:** 0.22 (function body across 4 matched files)
- **Average documentation cosine:** 0.39 (doc text across 4 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 7 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. util

- **Target:** `tempfile.Util [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2000210.0
- **Functions:** 2/2 matched (target 12)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/util.rs` vs expected `util.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/util.rs` vs expected `util.rs`
- **Proposed provenance header:** `// port-lint: source util.rs` (current: `// port-lint: source src/util.rs`)
- **Proposed provenance header:** `// port-lint: source util.rs` (current: `// port-lint: source src/util.rs`)
- **Lint issues:** 2

### 2. env

- **Target:** `tempfile.Env [PROVENANCE-FALLBACK]`
- **Similarity:** 0.49
- **Dependents:** 2
- **Priority Score:** 2000205.0
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/env.rs` vs expected `env.rs`
- **Proposed provenance header:** `// port-lint: source env.rs` (current: `// port-lint: source src/env.rs`)
- **Lint issues:** 1

### 3. error

- **Target:** `tempfile.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 0.22
- **Dependents:** 1
- **Priority Score:** 1030507.8
- **Functions:** 1/3 matched (target 2)
- **Missing functions:** `fmt`, `source`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `IoResultExt`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source src/error.rs`)
- **Lint issues:** 1

### 4. spooled

- **Target:** `tempfile.Spooled [PROVENANCE-FALLBACK]`
- **Similarity:** 0.18
- **Dependents:** 0
- **Priority Score:** 112108.2
- **Functions:** 8/19 matched (target 13)
- **Missing functions:** `cursor_to_tempfile`, `new`, `new_in`, `into_file`, `read_vectored`, `read_to_end`, `read_to_string`, `read_exact`, `write_vectored`, `flush`, `seek`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/spooled.rs` vs expected `spooled.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `tests:tests/spooled.rs` vs expected `spooled.rs`
- **Proposed provenance header:** `// port-lint: source spooled.rs` (current: `// port-lint: source src/spooled.rs`)
- **Proposed provenance header:** `// port-lint: tests spooled.rs` (current: `// port-lint: tests tests/spooled.rs`)
- **Lint issues:** 2

### 5. lib

- **Target:** `tempfile.Lib [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 81610.0
- **Functions:** 7/15 matched (target 7)
- **Missing functions:** `default`, `new`, `permissions`, `keep`, `tempfile`, `tempfile_in`, `make`, `make_in`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source src/lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source src/lib.rs`)
- **Lint issues:** 2

### 6. dir.mod

- **Target:** `dir.TempDir [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 61810.0
- **Functions:** 11/17 matched (target 13)
- **Missing functions:** `into_path`, `disable_cleanup`, `as_ref`, `fmt`, `drop`, `create`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/dir/mod.rs` vs expected `dir/mod.rs`
- **Proposed provenance header:** `// port-lint: source dir/mod.rs` (current: `// port-lint: source src/dir/mod.rs`)
- **Lint issues:** 1

### 7. imp.mod

- **Target:** `dir.TempDirTest [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 11)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only by basename: `src/dir/mod.rs` vs expected `dir/imp/mod.rs`
- **Proposed provenance header:** `// port-lint: source dir/imp/mod.rs` (current: `// port-lint: source src/dir/mod.rs`)
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

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `file.imp.mod` | `file.imp.Mod` | 0 | `file/imp/mod.rs` | `file/imp/Mod.kt` |
| `file.mod` | `file.Mod` | 0 | `file/mod.rs` | `file/Mod.kt` |

