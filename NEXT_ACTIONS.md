# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 6/14 (42.9%)
- **Function parity:** 31/154 matched (target 67) — 20.1%
- **Class/type parity:** 5/11 matched (target 14) — 45.5%
- **Combined symbol parity:** 36/165 matched (target 81) — 21.8%
- **Average inline-code cosine:** 0.38 (function body across 5 matched files)
- **Average documentation cosine:** 0.34 (doc text across 5 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 5 files with <0.60 function similarity

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
- **Similarity:** 0.22
- **Dependents:** 1
- **Priority Score:** 1030507.8
- **Functions:** 1/3 matched (target 8)
- **Missing functions:** `fmt`, `source`
- **Types:** 1/2 matched
- **Missing types:** `IoResultExt`

### 4. spooled

- **Target:** `tempfile.Spooled`
- **Similarity:** 0.18
- **Dependents:** 0
- **Priority Score:** 112108.2
- **Functions:** 8/19 matched (target 13)
- **Missing functions:** `cursor_to_tempfile`, `new`, `new_in`, `into_file`, `read_vectored`, `read_to_end`, `read_to_string`, `read_exact`, `write_vectored`, `flush`, `seek`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_

### 5. lib

- **Target:** `tempfile.Lib`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 81606.3
- **Functions:** 7/15 matched (target 8)
- **Missing functions:** `default`, `new`, `permissions`, `keep`, `tempfile`, `tempfile_in`, `make`, `make_in`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_

### 6. dir.mod

- **Target:** `dir.TempDir [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 61810.0
- **Functions:** 11/17 matched (target 24)
- **Missing functions:** `into_path`, `disable_cleanup`, `as_ref`, `fmt`, `drop`, `create`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

