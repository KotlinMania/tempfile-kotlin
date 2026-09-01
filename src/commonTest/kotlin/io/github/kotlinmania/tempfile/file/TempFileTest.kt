// port-lint: tests tempfile/tests/tempfile.rs
package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.dir.tempdir
import io.github.kotlinmania.tempfile.tempfile
import io.github.kotlinmania.tempfile.tempfileIn
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TempFileTest {
    @Test
    fun testBasic() {
        val tmpfile = tempfile().getOrThrow()
        val path = tmpfile.path()
        assertTrue(fileExists(path))
        tmpfile.close()
        assertFalse(fileExists(path))
    }

    @Test
    fun testCleanup() {
        val tmpdir = tempdir().getOrThrow()
        val tmpfile = tempfileIn(tmpdir.path()).getOrThrow()
        val path = tmpfile.path()
        assertTrue(fileExists(path))
        tmpfile.close()
        assertFalse(fileExists(path))
        tmpdir.close()
    }
}
