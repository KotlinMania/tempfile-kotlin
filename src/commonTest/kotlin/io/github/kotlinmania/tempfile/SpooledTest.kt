// port-lint: tests tempfile/tests/spooled.rs
package io.github.kotlinmania.tempfile

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpooledTest {
    @Test
    fun testAutomaticRollover() {
        val t = spooledTempfile(10)
        val buf = mutableListOf<Byte>()

        assertFalse(t.isRolled())
        assertEquals(0L, t.streamPosition().getOrThrow())
        assertEquals(0, t.readToEnd(buf))
        assertEquals(0, buf.size)
        buf.clear()

        assertEquals(5, t.write("abcde".encodeToByteArray()))
        assertFalse(t.isRolled())
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(5, t.readToEnd(buf))
        assertContentEquals("abcde".encodeToByteArray(), buf.toByteArray())

        assertEquals(10, t.write("fghijklmno".encodeToByteArray()))
        assertEquals(15L, t.streamPosition().getOrThrow())
        assertTrue(t.isRolled())
        t.close()
    }

    @Test
    fun testCustomDir() {
        run {
            val t = spooledTempfileIn(10, tempDir())
            assertTrue(t.roll().isSuccess, "failed to roll temp file in a specified directory")
            t.close()
        }

        run {
            val t = spooledTempfileIn(10, "/does-not-exist/")
            assertTrue(t.roll().isFailure, "should fail to roll the temporary file into a nonexistent tempdir")
            t.close()
        }
    }

    @Test
    fun testExplicitRollover() {
        val t = SpooledTempFile.new(100)
        assertEquals(26, t.write("abcdefghijklmnopqrstuvwxyz".encodeToByteArray()))
        assertEquals(26L, t.streamPosition().getOrThrow())
        assertFalse(t.isRolled())

        assertTrue(t.roll().isSuccess)
        assertTrue(t.isRolled())
        assertEquals(26L, t.streamPosition().getOrThrow())

        val buf = mutableListOf<Byte>()
        assertEquals(0, t.readToEnd(buf))
        assertEquals(0, buf.size)
        buf.clear()

        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(26, t.readToEnd(buf))
        assertContentEquals("abcdefghijklmnopqrstuvwxyz".encodeToByteArray(), buf.toByteArray())
        assertEquals(26L, t.streamPosition().getOrThrow())
        t.close()
    }

    private fun checkSeek(t: SpooledTempFile) {
        assertEquals(26, t.write("abcdefghijklmnopqrstuvwxyz".encodeToByteArray()))

        assertEquals(26L, t.streamPosition().getOrThrow())
        assertEquals(25L, t.seek(SeekFrom.Current(-1)).getOrThrow())
        assertEquals(26L, t.seek(SeekFrom.Current(1)).getOrThrow())
        assertEquals(27L, t.seek(SeekFrom.Current(1)).getOrThrow())
        assertEquals(0L, t.seek(SeekFrom.Current(-27)).getOrThrow())
        assertTrue(t.seek(SeekFrom.Current(-1)).isFailure)
        assertTrue(t.seek(SeekFrom.Current(-1245)).isFailure)

        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(1L, t.seek(SeekFrom.Start(1)).getOrThrow())
        assertEquals(26L, t.seek(SeekFrom.Start(26)).getOrThrow())
        assertEquals(27L, t.seek(SeekFrom.Start(27)).getOrThrow())

        assertEquals(26L, t.seek(SeekFrom.End(0)).getOrThrow())
        assertEquals(25L, t.seek(SeekFrom.End(-1)).getOrThrow())
        assertEquals(0L, t.seek(SeekFrom.End(-26)).getOrThrow())
        assertTrue(t.seek(SeekFrom.End(-27)).isFailure)
        assertTrue(t.seek(SeekFrom.End(-99)).isFailure)
        assertEquals(27L, t.seek(SeekFrom.End(1)).getOrThrow())
        assertEquals(27L, t.seek(SeekFrom.End(1)).getOrThrow())
        t.close()
    }

    @Test
    fun testSeekBuffer() {
        val t = spooledTempfile(100)
        checkSeek(t)
    }

    @Test
    fun testSeekFile() {
        val t = SpooledTempFile.new(10)
        checkSeek(t)
    }

    private fun checkSeekRead(t: SpooledTempFile) {
        assertEquals(26, t.write("abcdefghijklmnopqrstuvwxyz".encodeToByteArray()))

        val buf = mutableListOf<Byte>()
        assertEquals(0, t.readToEnd(buf))
        assertEquals(0, buf.size)
        buf.clear()

        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(26, t.readToEnd(buf))
        assertContentEquals("abcdefghijklmnopqrstuvwxyz".encodeToByteArray(), buf.toByteArray())
        buf.clear()

        assertEquals(26L, t.streamPosition().getOrThrow())
        assertEquals(0, t.readToEnd(buf))
        assertEquals(0, buf.size)
        buf.clear()

        assertEquals(5L, t.seek(SeekFrom.Start(5)).getOrThrow())
        val exactBuf = ByteArray(5)
        assertTrue(t.readExact(exactBuf).isSuccess)
        assertContentEquals("fghij".encodeToByteArray(), exactBuf)

        assertEquals(10L, t.streamPosition().getOrThrow())
        assertTrue(t.readExact(exactBuf).isSuccess)
        assertContentEquals("klmno".encodeToByteArray(), exactBuf)

        val partialBuf = ByteArray(15)
        assertEquals(11, t.read(partialBuf))
        assertContentEquals("pqrstuvwxyz".encodeToByteArray(), partialBuf.copyOfRange(0, 11))

        assertTrue(t.readExact(partialBuf).isFailure)
        t.close()
    }

    @Test
    fun testSeekReadBuffer() {
        val t = spooledTempfile(100)
        checkSeekRead(t)
    }

    @Test
    fun testSeekReadFile() {
        val t = SpooledTempFile.new(10)
        checkSeekRead(t)
    }

    private fun checkOverwriteMiddle(t: SpooledTempFile) {
        assertEquals(26, t.write("abcdefghijklmnopqrstuvwxyz".encodeToByteArray()))
        assertEquals(10L, t.seek(SeekFrom.Start(10)).getOrThrow())
        assertEquals(10, t.write("0123456789".encodeToByteArray()))
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())

        val buf = mutableListOf<Byte>()
        assertEquals(26, t.readToEnd(buf))
        assertContentEquals("abcdefghij0123456789uvwxyz".encodeToByteArray(), buf.toByteArray())
        t.close()
    }

    @Test
    fun testOverwriteMiddleOfBuffer() {
        val t = spooledTempfile(100)
        checkOverwriteMiddle(t)
    }

    @Test
    fun testOverwriteMiddleOfFile() {
        val t = SpooledTempFile.new(10)
        checkOverwriteMiddle(t)
    }

    @Test
    fun testOverwriteAndExtendBuffer() {
        val t = spooledTempfile(100)
        assertEquals(26, t.write("abcdefghijklmnopqrstuvwxyz".encodeToByteArray()))
        assertEquals(21L, t.seek(SeekFrom.End(-5)).getOrThrow())
        assertEquals(10, t.write("0123456789".encodeToByteArray()))
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        val buf = mutableListOf<Byte>()
        assertEquals(31, t.readToEnd(buf))
        assertContentEquals("abcdefghijklmnopqrstu0123456789".encodeToByteArray(), buf.toByteArray())
        assertFalse(t.isRolled())
        t.close()
    }

    @Test
    fun testOverwriteAndExtendRollover() {
        val t = SpooledTempFile.new(20)
        assertEquals(15, t.write("abcdefghijklmno".encodeToByteArray()))
        assertFalse(t.isRolled())
        assertEquals(10L, t.seek(SeekFrom.End(-5)).getOrThrow())
        assertEquals(10L, t.streamPosition().getOrThrow())
        assertFalse(t.isRolled())
        assertEquals(20, t.write("0123456789)!@#$%^&*(".encodeToByteArray()))
        assertTrue(t.isRolled())
        assertEquals(30L, t.streamPosition().getOrThrow())
        val buf = mutableListOf<Byte>()
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(30, t.readToEnd(buf))
        assertContentEquals("abcdefghij0123456789)!@#$%^&*(".encodeToByteArray(), buf.toByteArray())
        t.close()
    }

    private fun checkSparse(t: SpooledTempFile) {
        assertEquals(5, t.write("abcde".encodeToByteArray()))
        assertEquals(10L, t.seek(SeekFrom.Current(5)).getOrThrow())
        assertEquals(5, t.write("klmno".encodeToByteArray()))
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        val buf = mutableListOf<Byte>()
        assertEquals(15, t.readToEnd(buf))
        val expected = ByteArray(15)
        "abcde".encodeToByteArray().copyInto(expected, 0)
        "klmno".encodeToByteArray().copyInto(expected, 10)
        assertContentEquals(expected, buf.toByteArray())
        t.close()
    }

    @Test
    fun testSparseBuffer() {
        val t = spooledTempfile(100)
        checkSparse(t)
    }

    @Test
    fun testSparseFile() {
        val t = SpooledTempFile.new(1)
        checkSparse(t)
    }

    @Test
    fun testSparseWriteRollover() {
        val t = spooledTempfile(10)
        assertEquals(5, t.write("abcde".encodeToByteArray()))
        assertFalse(t.isRolled())
        assertEquals(10L, t.seek(SeekFrom.Current(5)).getOrThrow())
        assertFalse(t.isRolled())
        assertEquals(5, t.write("klmno".encodeToByteArray()))
        assertTrue(t.isRolled())
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        val buf = mutableListOf<Byte>()
        assertEquals(15, t.readToEnd(buf))
        val expected = ByteArray(15)
        "abcde".encodeToByteArray().copyInto(expected, 0)
        "klmno".encodeToByteArray().copyInto(expected, 10)
        assertContentEquals(expected, buf.toByteArray())
        t.close()
    }

    private fun checkSetLen(t: SpooledTempFile) {
        val buf = mutableListOf<Byte>()
        assertEquals(26, t.write("abcdefghijklmnopqrstuvwxyz".encodeToByteArray()))
        assertTrue(t.setLen(10).isSuccess)
        assertEquals(26L, t.streamPosition().getOrThrow())

        assertEquals(0, t.readToEnd(buf))
        assertEquals(0, buf.size)
        assertEquals(26L, t.streamPosition().getOrThrow())
        buf.clear()

        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(10, t.readToEnd(buf))
        assertContentEquals("abcdefghij".encodeToByteArray(), buf.toByteArray())
        buf.clear()

        assertTrue(t.setLen(40).isSuccess)
        assertEquals(10L, t.streamPosition().getOrThrow())
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(40, t.readToEnd(buf))
        val expected = ByteArray(40)
        "abcdefghij".encodeToByteArray().copyInto(expected, 0)
        assertContentEquals(expected, buf.toByteArray())
        t.close()
    }

    @Test
    fun testSetLenBuffer() {
        val t = spooledTempfile(100)
        checkSetLen(t)
    }

    @Test
    fun testSetLenFile() {
        val t = spooledTempfile(100)
        checkSetLen(t)
    }

    @Test
    fun testSetLenRollover() {
        val buf = mutableListOf<Byte>()
        val t = spooledTempfile(10)
        assertEquals(5, t.write("abcde".encodeToByteArray()))
        assertFalse(t.isRolled())
        assertEquals(5L, t.streamPosition().getOrThrow())

        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(5, t.readToEnd(buf))
        assertContentEquals("abcde".encodeToByteArray(), buf.toByteArray())
        assertEquals(5L, t.streamPosition().getOrThrow())
        buf.clear()

        assertTrue(t.setLen(20).isSuccess)
        assertTrue(t.isRolled())
        assertEquals(5L, t.streamPosition().getOrThrow())
        assertEquals(0L, t.seek(SeekFrom.Start(0)).getOrThrow())
        assertEquals(20, t.readToEnd(buf))
        val expected = ByteArray(20)
        "abcde".encodeToByteArray().copyInto(expected, 0)
        assertContentEquals(expected, buf.toByteArray())
        t.close()
    }

    @Test
    fun testWriteOverflow() {
        val t = spooledTempfile(10)
        t.seek(SeekFrom.Start(Long.MAX_VALUE)).getOrThrow()
        assertFailsWith<IoException> {
            t.write("abcde".encodeToByteArray())
        }
        t.close()
    }

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
        spooled.close()
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
        spooled.close()
    }
}
