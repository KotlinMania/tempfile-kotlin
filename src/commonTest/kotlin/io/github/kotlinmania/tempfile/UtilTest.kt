// port-lint: tests util.rs
package io.github.kotlinmania.tempfile

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UtilTest {
    @Test
    fun tmpnameComposesPrefixRandomSuffix() {
        val rng = Random(seed = 12345L)
        val name = tmpname(rng, prefix = ".tmp", suffix = ".log", randLen = 6)
        assertTrue(name.startsWith(".tmp"), "missing prefix in $name")
        assertTrue(name.endsWith(".log"), "missing suffix in $name")
        assertEquals(".tmp".length + 6 + ".log".length, name.length)
        val random = name.substring(".tmp".length, name.length - ".log".length)
        for (c in random) {
            assertTrue(c.isLetterOrDigit(), "non-alphanumeric '$c' in $random")
        }
    }

    @Test
    fun tmpnameWithZeroRandLenJustConcatenates() {
        val rng = Random(seed = 1L)
        assertEquals("abcXYZ", tmpname(rng, "abc", "XYZ", 0))
    }

    @Test
    fun tmpnameEmptyPrefixAndSuffix() {
        val rng = Random(seed = 99L)
        val name = tmpname(rng, "", "", 6)
        assertEquals(6, name.length)
    }

    @Test
    fun tmpnameSeededReproducible() {
        val a = tmpname(Random(42L), "p_", "_s", 8)
        val b = tmpname(Random(42L), "p_", "_s", 8)
        assertEquals(a, b)
    }

    @Test
    fun isAbsolutePathRecognisesUnixRoot() {
        assertTrue(isAbsolutePath("/tmp"))
        assertTrue(isAbsolutePath("/"))
    }

    @Test
    fun isAbsolutePathRecognisesWindowsDrive() {
        assertTrue(isAbsolutePath("C:/Users"))
        assertTrue(isAbsolutePath("C:\\Users"))
        assertTrue(isAbsolutePath("d:/x"))
    }

    @Test
    fun isAbsolutePathRejectsRelative() {
        assertEquals(false, isAbsolutePath("tmp"))
        assertEquals(false, isAbsolutePath("./tmp"))
        assertEquals(false, isAbsolutePath(""))
        assertEquals(false, isAbsolutePath("C:tmp"))
    }

    @Test
    fun joinPathInsertsSeparator() {
        assertEquals("/tmp/x", joinPath("/tmp", "x"))
        assertEquals("/tmp/x", joinPath("/tmp/", "x"))
        assertEquals("/abs", joinPath("/tmp", "/abs"))
        assertEquals("a", joinPath("", "a"))
    }
}
