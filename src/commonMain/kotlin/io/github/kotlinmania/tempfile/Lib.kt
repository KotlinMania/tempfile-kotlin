// port-lint: source lib.rs
package io.github.kotlinmania.tempfile

import io.github.kotlinmania.tempfile.dir.TempDir
import io.github.kotlinmania.tempfile.file.NamedTempFile
import io.github.kotlinmania.tempfile.dir.tempdir as dirTempdir
import io.github.kotlinmania.tempfile.dir.tempdirIn as dirTempdirIn
import io.github.kotlinmania.tempfile.file.tempfile as fileTempfile
import io.github.kotlinmania.tempfile.file.tempfileIn as fileTempfileIn

/**
 * Top-level tempfile namespace module.
 */
public object Tempfile {
    public const val VERSION: String = "3.17.1"
}

/**
 * Create a new temporary file.
 */
public fun tempfile(): Result<NamedTempFile<String>> = fileTempfile()

/**
 * Create a new temporary file in the specified directory.
 */
public fun tempfileIn(dir: String): Result<NamedTempFile<String>> = fileTempfileIn(dir)

/**
 * Create a new temporary directory.
 */
public fun tempdir(): Result<TempDir> = dirTempdir()

/**
 * Create a new temporary directory inside the specified [dir].
 */
public fun tempdirIn(dir: String): Result<TempDir> = dirTempdirIn(dir)
