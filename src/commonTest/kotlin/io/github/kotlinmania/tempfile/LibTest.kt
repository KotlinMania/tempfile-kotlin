// port-lint: tests lib.rs
package io.github.kotlinmania.tempfile

import kotlin.test.Test
import kotlin.test.assertEquals

class LibTest {
    @Test
    fun testTempfileVersion() {
        assertEquals("3.17.1", Tempfile.VERSION)
    }
}
