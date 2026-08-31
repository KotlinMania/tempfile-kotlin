// port-lint: tests tests/namedtempfile.rs
package io.github.kotlinmania.tempfile.file

import io.github.kotlinmania.tempfile.Builder
import io.github.kotlinmania.tempfile.dir.TempDir
import io.github.kotlinmania.tempfile.dir.tempdir
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NamedTempFileTest {

    @Test
    fun testPrefix() {
        val tmpfile = NamedTempFile.withPrefix("prefix").getOrThrow()
        val path = tmpfile.path()
        val filename = path.substringAfterLast('/').substringAfterLast('\\')
        assertTrue(filename.startsWith("prefix"), "expected filename to start with 'prefix', got $filename")
        tmpfile.close()
    }

    @Test
    fun testSuffix() {
        val tmpfile = NamedTempFile.withSuffix(".txt").getOrThrow()
        val path = tmpfile.path()
        val filename = path.substringAfterLast('/').substringAfterLast('\\')
        assertTrue(filename.endsWith(".txt"), "expected filename to end with '.txt', got $filename")
        tmpfile.close()
    }

    @Test
    fun testBasic() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val path = tmpfile.path()
        assertTrue(fileExists(path))
        tmpfile.close()
        assertFalse(fileExists(path))
    }

    @Test
    fun testDeleted() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val path = tmpfile.path()
        assertTrue(fileExists(path))
        tmpfile.close()
        assertFalse(fileExists(path))
    }

    @Test
    fun testPersist() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val oldPath = tmpfile.path()
        val persistPath = oldPath + "_persisted"
        assertTrue(fileExists(oldPath))

        val f = tmpfile.persist(persistPath).getOrThrow()
        assertFalse(fileExists(oldPath))
        assertTrue(fileExists(persistPath))

        removeFile(persistPath)
    }

    @Test
    fun testPersistNoclobber() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val oldPath = tmpfile.path()
        val persistTarget = NamedTempFile.new().getOrThrow()
        val persistPath = persistTarget.path()

        assertTrue(fileExists(oldPath))
        assertTrue(fileExists(persistPath))

        val errRes = tmpfile.persistNoclobber(persistPath)
        assertTrue(errRes.isFailure, "persistNoclobber should fail when target exists")
        assertTrue(fileExists(oldPath))

        persistTarget.close()
        assertFalse(fileExists(persistPath))

        val okRes = tmpfile.persistNoclobber(persistPath)
        assertTrue(okRes.isSuccess)
        assertTrue(fileExists(persistPath))
        assertFalse(fileExists(oldPath))

        removeFile(persistPath)
    }

    @Test
    fun testCustomNamed() {
        val tmpfile = Builder()
            .prefix("tmp_test_")
            .suffix(".rs")
            .randBytes(12)
            .tempfile()
            .getOrThrow()

        val name = tmpfile.path().substringAfterLast('/').substringAfterLast('\\')
        assertTrue(name.startsWith("tmp_test_"))
        assertTrue(name.endsWith(".rs"))
        tmpfile.close()
    }

    @Test
    fun testIntoParts() {
        val file = NamedTempFile.new().getOrThrow()
        val (handle, tempPath) = file.intoParts()
        val path = tempPath.path()

        assertTrue(fileExists(path))
        tempPath.close()
        assertFalse(fileExists(path))
    }

    @Test
    fun testFromParts() {
        val file = NamedTempFile.new().getOrThrow()
        val (handle, tempPath) = file.intoParts()
        val reconstructed = NamedTempFile.fromParts(handle, tempPath)
        val path = reconstructed.path()

        assertTrue(fileExists(path))
        reconstructed.close()
        assertFalse(fileExists(path))
    }

    @Test
    fun testKeep() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val (f, tempPath) = tmpfile.intoParts()
        val path = tempPath.keep().getOrThrow()

        assertTrue(fileExists(path))
        removeFile(path)
        assertFalse(fileExists(path))
    }

    @Test
    fun testDisableCleanup() {
        for (case in 0 until 4) {
            val inBuilder = (case and 1) > 0
            val toggle = (case and 2) > 0
            val tmpfile = Builder()
                .disableCleanup(inBuilder)
                .tempfile()
                .getOrThrow()

            if (toggle) {
                tmpfile.disableCleanup(!inBuilder)
            }

            val path = tmpfile.path()
            tmpfile.close()

            if (inBuilder xor toggle) {
                assertTrue(fileExists(path), "expected file to be kept for case $case")
                removeFile(path)
            } else {
                assertFalse(fileExists(path), "expected file to be deleted for case $case")
            }
        }
    }

    @Test
    fun testMake() {
        val tmpfile = Builder().make { path -> Result.success(path) }.getOrThrow()
        val path = tmpfile.path()
        assertTrue(fileExists(path))
        tmpfile.close()
        assertFalse(fileExists(path))
    }

    @Test
    fun testMakeIn() {
        val tmpDir = tempdir().getOrThrow()
        val tmpfile = Builder().makeIn(tmpDir.path()) { path -> Result.success(path) }.getOrThrow()
        val path = tmpfile.path()

        assertTrue(fileExists(path))
        tmpfile.close()
        tmpDir.close()
    }

    @Test
    fun testTempPath() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val pathHandle = tmpfile.intoTempPath()
        val path = pathHandle.path()
        assertTrue(fileExists(path))
        pathHandle.close()
        assertFalse(fileExists(path))
    }


    @Test
    fun testTempPathPersist() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val tmppath = tmpfile.intoTempPath()
        val oldPath = tmppath.path()
        val persistPath = oldPath + "_persisted_tmppath"

        assertTrue(fileExists(oldPath))
        tmppath.persist(persistPath).getOrThrow()
        assertFalse(fileExists(oldPath))
        assertTrue(fileExists(persistPath))

        removeFile(persistPath)
    }

    @Test
    fun testTempPathPersistNoclobber() {
        val tmpfile = NamedTempFile.new().getOrThrow()
        val tmppath = tmpfile.intoTempPath()
        val oldPath = tmppath.path()
        val persistTarget = NamedTempFile.new().getOrThrow()
        val persistPath = persistTarget.path()

        assertTrue(fileExists(oldPath))
        assertTrue(fileExists(persistPath))

        val errRes = tmppath.persistNoclobber(persistPath)
        assertTrue(errRes.isFailure)

        persistTarget.close()
        assertFalse(fileExists(persistPath))

        tmppath.persistNoclobber(persistPath).getOrThrow()
        assertTrue(fileExists(persistPath))
        assertFalse(fileExists(oldPath))

        removeFile(persistPath)
    }

    @Test
    fun tempPathFromExisting() {
        val tmpDir = tempdir().getOrThrow()
        val tmpFilePath1 = tmpDir.path() + "/testfile1"
        val tmpFilePath2 = tmpDir.path() + "/testfile2"

        createNamedFile(tmpFilePath1).getOrThrow()
        assertTrue(fileExists(tmpFilePath1), "Test file 1 hasn't been created")

        createNamedFile(tmpFilePath2).getOrThrow()
        assertTrue(fileExists(tmpFilePath2), "Test file 2 hasn't been created")

        val tmpPath = TempPath.tryFromPath(tmpFilePath1).getOrThrow()
        assertTrue(fileExists(tmpFilePath1), "Test file has been deleted before closing TempPath")

        tmpPath.close().getOrThrow()
        assertFalse(fileExists(tmpFilePath1), "Test file exists after closing TempPath")
        assertTrue(fileExists(tmpFilePath2), "Test file 2 has been deleted before closing TempDir")

        tmpDir.close().getOrThrow()
    }
}
