# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 6/19 (31.6%)
- **Function parity:** 21/232 matched (target 67) — 9.1%
- **Class/type parity:** 3/13 matched (target 14) — 23.1%
- **Combined symbol parity:** 24/245 matched (target 81) — 9.8%
- **Average inline-code cosine:** 0.21 (function body across 4 matched files)
- **Average documentation cosine:** 0.01 (doc text across 4 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 5 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. tempfile.util

- **Target:** `tempfile.Util`
- **Similarity:** 0.63
- **Dependents:** 2
- **Priority Score:** 2000203.8
- **Functions:** 2/2 matched (target 12)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 2. tempfile.error

- **Target:** `tempfile.Error`
- **Similarity:** 0.22
- **Dependents:** 1
- **Priority Score:** 1030507.8
- **Functions:** 1/3 matched (target 8)
- **Missing functions:** `fmt`, `source`
- **Types:** 1/2 matched
- **Missing types:** `IoResultExt`

### 3. tests.spooled

- **Target:** `tempfile.Spooled`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 252510.0
- **Functions:** 0/25 matched (target 13)
- **Missing functions:** `configure_wasi_temp_dir`, `test_automatic_rollover`, `test_custom_dir`, `test_explicit_rollover`, `test_seek`, `test_seek_buffer`, `test_seek_file`, `test_seek_read`, `test_seek_read_buffer`, `test_seek_read_file`, `test_overwrite_middle`, `test_overwrite_middle_of_buffer`, `test_overwrite_middle_of_file`, `test_overwrite_and_extend_buffer`, `test_overwrite_and_extend_rollover`, `test_sparse`, `test_sparse_buffer`, `test_sparse_file`, `test_sparse_write_rollover`, `test_set_len`, `test_set_len_buffer`, `test_set_len_file`, `test_set_len_rollover`, `test_write_overflow`, `test_set_len_truncation`
- **Types:** 0/0 matched (target 5)
- **Missing types:** _none_
- **Tests:** 0/19 matched

### 4. tempfile.lib

- **Target:** `tempfile.Lib [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 81610.0
- **Functions:** 7/15 matched (target 8)
- **Missing functions:** `default`, `new`, `permissions`, `keep`, `tempfile`, `tempfile_in`, `make`, `make_in`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_

### 5. dir.mod

- **Target:** `dir.TempDir [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 61810.0
- **Functions:** 11/17 matched (target 24)
- **Missing functions:** `into_path`, `disable_cleanup`, `as_ref`, `fmt`, `drop`, `create`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 6. tests.env

- **Target:** `tempfile.Env`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 2)
- **Missing functions:** `test_override_temp_dir`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/1 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

