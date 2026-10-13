// port-lint: tests tempfile/tests/env.rs
package io.github.kotlinmania.tempfile

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnvTest {
    @AfterTest
    fun tearDown() {
        resetTempDirForTesting()
    }

    @Test
    fun testOverrideTempDir() {
        assertEquals(tempDir(), systemTempDir())

        val newTmp = "/tmp/override"
        val firstRes = overrideTempDir(newTmp)
        assertTrue(firstRes.isSuccess, "first overrideTempDir should succeed")
        assertEquals(newTmp, tempDir())

        val newTmp2 = "/tmp/override2"
        val secondRes = overrideTempDir(newTmp2)
        assertTrue(secondRes.isFailure, "override should only be possible once")
        val exception = secondRes.exceptionOrNull()
        assertTrue(exception is OverrideTempDirAlreadySet)
        assertEquals(newTmp, exception.previouslySet)
    }
}
