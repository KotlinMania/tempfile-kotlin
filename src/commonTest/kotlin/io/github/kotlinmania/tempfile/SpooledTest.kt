// port-lint: tests tempfile/src/spooled.rs
package io.github.kotlinmania.tempfile

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpooledTest {
    @Test
    fun testSpooledInMemoryRollover() {
        val spooled = spooledTempfile(10)
        assertFalse(spooled.isRolled())

        val smallData = "hello".encodeToByteArray()
        spooled.write(smallData)
        assertFalse(spooled.isRolled())

        val largeData = " world extended".encodeToByteArray()
        spooled.write(largeData)
        assertTrue(spooled.isRolled())
    }

    @Test
    fun testSpooledReadWrite() {
        val spooled = spooledTempfile(100)
        val data = "test data stream".encodeToByteArray()
        val written = spooled.write(data)
        assertEquals(data.size, written)

        val inner = spooled.intoInner()
        assertTrue(inner is SpooledData.InMemory)
        assertEquals(data.size, inner.buffer.size)
    }
}
