# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 14/14 (100.0%)
- **Function parity:** 74/111 matched (target 158) — 66.7%
- **Class/type parity:** 9/10 matched (target 16) — 90.0%
- **Combined symbol parity:** 83/121 matched (target 174) — 68.6%
- **Average inline-code cosine:** 0.32 (function body across 10 matched files)
- **Average documentation cosine:** 0.21 (doc text across 10 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 12 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. env

- **Target:** `tempfile.Env`
- **Similarity:** 0.49
- **Dependents:** 2
- **Priority Score:** 2000205.0
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 2. util

- **Target:** `tempfile.Util`
- **Similarity:** 0.63
- **Dependents:** 2
- **Priority Score:** 2000203.8
- **Functions:** 2/2 matched (target 12)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 3. error

- **Target:** `tempfile.Error`
- **Similarity:** 0.48
- **Dependents:** 1
- **Priority Score:** 1000505.1
- **Functions:** 3/3 matched (target 10)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_

### 4. file.mod

- **Target:** `file.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 185010.0
- **Functions:** 28/45 matched (target 47)
- **Missing functions:** `from`, `deref`, `read`, `read_vectored`, `read_to_end`, `read_to_string`, `read_exact`, `write`, `flush`, `write_vectored`, `write_all`, `write_fmt`, `seek`, `as_fd`, `as_raw_fd`, `as_handle`, `as_raw_handle`
- **Types:** 4/5 matched (target 4)
- **Missing types:** `Target`

### 5. imp.windows

- **Target:** `imp.Windows`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 70710.0
- **Functions:** 0/7 matched (target 5)
- **Missing functions:** `to_utf16`, `not_supported`, `create_named`, `create`, `reopen`, `keep`, `persist`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 6. file.imp.unix

- **Target:** `commonMain.kotlin.io.github.kotlinmania.tempfile.file.imp.Unix`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 60709.4
- **Functions:** 1/7 matched (target 5)
- **Missing functions:** `create_named`, `create_unlinked`, `create`, `reopen`, `persist`, `keep`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 7. imp.other

- **Target:** `imp.Other`
- **Similarity:** 0.05
- **Dependents:** 0
- **Priority Score:** 50609.5
- **Functions:** 1/6 matched (target 11)
- **Missing functions:** `create_named`, `create`, `reopen`, `persist`, `keep`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 8. imp.any

- **Target:** `imp.Any`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 10207.0
- **Functions:** 1/2 matched (target 3)
- **Missing functions:** `create`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 9. imp.unix

- **Target:** `imp.Unix`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched
- **Missing functions:** `create`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 10. spooled

- **Target:** `tempfile.Spooled`
- **Similarity:** 0.42
- **Dependents:** 0
- **Priority Score:** 2105.8
- **Functions:** 19/19 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_

### 11. dir.mod

- **Target:** `dir.TempDir [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1810.0
- **Functions:** 17/17 matched (target 31)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 12. imp.mod

- **Target:** `imp.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 1)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 13. file.imp.mod

- **Target:** `commonMain.kotlin.io.github.kotlinmania.tempfile.file.imp.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 5)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

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

