// port-lint: source src/dir/mod.rs
package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.Builder
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
