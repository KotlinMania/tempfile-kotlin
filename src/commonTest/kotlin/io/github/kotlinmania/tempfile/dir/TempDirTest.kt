// port-lint: tests tempfile/tests/tempdir.rs
package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.Builder
import io.github.kotlinmania.tempfile.file.fileExists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TempDirTest {
    private val created = mutableListOf<TempDir>()

    private fun track(d: Result<TempDir>): TempDir {
        val td = d.getOrThrow()
        created += td
        return td
    }

    @AfterTest
    fun cleanup() {
        for (d in created) {
            runCatching { d.close() }
        }
    }

    @Test
    fun testTempdir() {
        val path =
            run {
                val p = Builder().prefix("foobar").tempdir().getOrThrow()
                val pPath = p.path()
                assertTrue(pPath.contains("foobar"))
                p.close().getOrThrow()
                pPath
            }
        assertFalse(fileExists(path))
    }

    @Test
    fun testPrefix() {
        val tmpfile = track(TempDir.withPrefix("prefix"))
        val name = tmpfile.path().substringAfterLast('/').substringAfterLast('\\')
        assertTrue(name.startsWith("prefix"))
    }

    @Test
    fun testSuffix() {
        val tmpfile = track(TempDir.withSuffix("suffix"))
        val name = tmpfile.path().substringAfterLast('/').substringAfterLast('\\')
        assertTrue(name.endsWith("suffix"))
    }

    @Test
    fun testCustomnamed() {
        val tmpfile =
            track(
                Builder()
                    .prefix("prefix")
                    .suffix("suffix")
                    .randBytes(12)
                    .tempdir(),
            )
        val name = tmpfile.path().substringAfterLast('/').substringAfterLast('\\')
        assertTrue(name.startsWith("prefix"))
        assertTrue(name.endsWith("suffix"))
        assertEquals(24, name.length)
    }

    @Test
    fun testTempdirKeep() {
        val tmp = TempDir.new().getOrThrow()
        val path = tmp.keep()
        assertTrue(fileExists(path))
        removeDirAll(path).getOrThrow()
        assertFalse(fileExists(path))
    }

    @Test
    fun testTmpdirClose() {
        val tmp = TempDir.new().getOrThrow()
        val path = tmp.path()
        assertTrue(fileExists(path))
        tmp.close().getOrThrow()
        assertFalse(fileExists(path))
    }

    @Test
    fun passAsAsrefPath() {
        val td = track(tempdir())
        assertTrue(fileExists(td.asRef()))
        assertTrue(fileExists(td.asPath()))
        assertEquals(td.path(), td.asRef())
        assertEquals(td.path(), td.asPath())
    }

    @Test
    fun testDisableCleanupFourCases() {
        // Case 0: never mark as disable cleanup
        // Case 1: enable disable cleanup in the builder, don't touch it after.
        // Case 2: enable disable cleanup in the builder, turn it off after.
        // Case 3: don't enable disable cleanup in the builder, turn it on after.
        for (case in 0 until 4) {
            val inBuilder = (case and 1) > 0
            val toggle = (case and 2) > 0
            val tmpdir =
                Builder()
                    .disableCleanup(inBuilder)
                    .tempdir()
                    .getOrThrow()
            if (toggle) {
                tmpdir.disableCleanup(!inBuilder)
            }
            val path = tmpdir.path()
            tmpdir.close().getOrThrow()

            if (inBuilder xor toggle) {
                assertTrue(fileExists(path), "expected dir to be kept for case $case")
                removeDirAll(path).getOrThrow()
                assertFalse(fileExists(path))
            } else {
                assertFalse(fileExists(path), "expected dir to be deleted for case $case")
            }
        }
    }

    // Upstream test_rm_tempdir_threading and dont_double_panic test Rust thread::spawn panic unwinding
    // past TempDir drop handlers. Kotlin Multiplatform does not support cross-platform thread panic
    // unwinding past destructors.

    @Test
    fun tempdirCreatesUnderSystemTempDir() {
        val td = track(tempdir())
        val path = td.path()
        assertTrue(path.isNotEmpty(), "path must not be empty")
        assertTrue(
            path.contains(".tmp") || path.contains("/tmp/") || path.contains("Temp"),
            "path '$path' should be under a temp-shaped parent",
        )
    }

    @Test
    fun newProducesDistinctPaths() {
        val a = track(tempdir())
        val b = track(tempdir())
        assertTrue(a.path() != b.path(), "two consecutive tempdir() must give distinct paths")
    }

    @Test
    fun builderPrefixIsHonoured() {
        val td = track(Builder().prefix("myprefix-").tempdir())
        val name = td.path().substringAfterLast('/').substringAfterLast('\\')
        assertTrue(name.startsWith("myprefix-"), "expected prefix in $name")
    }

    @Test
    fun builderSuffixIsHonoured() {
        val td = track(Builder().prefix("p").suffix(".log").tempdir())
        val name = td.path().substringAfterLast('/').substringAfterLast('\\')
        assertTrue(name.endsWith(".log"), "expected .log suffix in $name")
    }

    @Test
    fun closeIsIdempotent() {
        val td = track(tempdir())
        val first = td.close()
        val second = td.close()
        assertTrue(first.isSuccess, "first close should succeed: ${first.exceptionOrNull()}")
        assertTrue(second.isSuccess, "second close on empty path should succeed")
    }

    @Test
    fun keepDisablesCleanupAndReturnsPath() {
        val td = track(tempdir())
        val kept = td.keep()
        assertTrue(kept.isNotEmpty(), "kept path should not be empty")
        assertEquals(true, td.disableCleanup, "keep() should set disableCleanup")
        // Manually remove the kept directory so we don't litter the test runner.
        removeDirAll(kept)
    }

    @Test
    fun tempdirInUsesGivenDir() {
        val parent = track(tempdir()).path()
        val child = track(tempdirIn(parent))
        assertTrue(
            child.path().startsWith(parent),
            "child '${child.path()}' should be under parent '$parent'",
        )
    }

    @Test
    fun withPrefixInUsesGivenDirAndPrefix() {
        val parent = track(tempdir()).path()
        val td = track(TempDir.withPrefixIn("with-", parent))
        assertTrue(td.path().startsWith(parent))
        val name = td.path().substringAfterLast('/').substringAfterLast('\\')
        assertTrue(name.startsWith("with-"), "expected with- prefix in $name")
    }

    @Test
    fun disableCleanupSkipsClose() {
        val td = track(tempdir())
        td.disableCleanup = true
        val path = td.path()
        val result = td.close()
        assertTrue(result.isSuccess)
        // The path is preserved by close(); manually clean up.
        val cleanupResult = assertNotNull(removeDirAll(path))
        assertTrue(cleanupResult.isSuccess, "manual cleanup should succeed: ${cleanupResult.exceptionOrNull()}")
    }
}
