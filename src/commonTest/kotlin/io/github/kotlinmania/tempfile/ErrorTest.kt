// port-lint: tests tempfile/src/error.rs
package io.github.kotlinmania.tempfile

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ErrorTest {
    @Test
    fun withErrPathSuccessIsUnchanged() {
        val r: Result<Int> = Result.success(7)
        val out = r.withErrPath { "/tmp/whatever" }
        assertEquals(7, out.getOrThrow())
    }

    @Test
    fun withErrPathWrapsIoExceptionAndPreservesKind() {
        val original = IoException(IoErrorKind.AlreadyExists, "file exists")
        val r: Result<Int> = Result.failure(original)

        val wrapped = r.withErrPath { "/tmp/foo" }
        val e = wrapped.exceptionOrNull()
        assertNotNull(e)
        assertTrue(e is IoException)
        assertEquals(IoErrorKind.AlreadyExists, e.kind)
        assertEquals("file exists at path \"/tmp/foo\"", e.message)
    }

    @Test
    fun withErrPathSourceChainSkipsTheWrappedIoException() {
        val root = RuntimeException("root cause")
        val carrier = RuntimeException("denied", root)
        val original = IoException(IoErrorKind.PermissionDenied, carrier)
        assertSame(root, original.cause)

        val wrapped = Result.failure<Int>(original).withErrPath { "/etc" }
        val e = wrapped.exceptionOrNull() as IoException
        assertSame(root, e.cause)
    }

    @Test
    fun withErrPathLeavesNonIoExceptionUnchanged() {
        val other = IllegalStateException("nope")
        val r: Result<Int> = Result.failure(other)
        val out = r.withErrPath { "/tmp/x" }
        assertSame(other, out.exceptionOrNull())
    }

    @Test
    fun displayMatchesRustOrderingErrAtPathQuoted() {
        val original = IoException(IoErrorKind.NotFound, "no such file")
        val wrapped = Result.failure<Int>(original).withErrPath { "/missing" }
        val msg = wrapped.exceptionOrNull()!!.message
        assertEquals("no such file at path \"/missing\"", msg)
    }

    @Test
    fun ioExceptionFromPayloadUsesPayloadToStringAndMessage() {
        val payload = RuntimeException("payload text")
        val e = IoException(IoErrorKind.Other, payload)
        assertEquals(payload.toString(), e.toString())
        assertEquals("payload text", e.message)
        assertNull(e.cause)
    }
}
