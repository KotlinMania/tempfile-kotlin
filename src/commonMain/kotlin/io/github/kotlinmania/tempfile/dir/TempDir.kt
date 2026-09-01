// port-lint: source dir/mod.rs
package io.github.kotlinmania.tempfile.dir

import io.github.kotlinmania.tempfile.Builder

/**
 * Create a new temporary directory. Also see [tempdirIn].
 *
 * The `tempdir` function creates a directory in the file system and returns
 * a [TempDir]. The directory will be automatically deleted when the returned
 * `TempDir` is closed.
 */
fun tempdir(): Result<TempDir> = TempDir.new()

/**
 * Create a new temporary directory inside the specified [dir]. Also see
 * [tempdir].
 *
 * The directory and everything inside it will be automatically deleted once
 * the returned [TempDir] is closed.
 */
fun tempdirIn(dir: String): Result<TempDir> = TempDir.newIn(dir)

/**
 * A directory in the filesystem that is automatically deleted when
 * it goes out of scope.
 *
 * The [TempDir] type creates a directory on the file system that
 * is deleted once it goes out of scope. At construction, the
 * `TempDir` creates a new directory with a randomly generated name.
 *
 * The default constructor [TempDir.new] creates directories in the
 * location returned by [io.github.kotlinmania.tempfile.tempDir], but
 * `TempDir` can be configured to manage a temporary directory in any
 * location by constructing with a [Builder].
 *
 * After creating a `TempDir`, work with the file system by doing standard
 * file operations on its [path], which can be retrieved with [path]. Once
 * the `TempDir` value is closed, the directory at the path will be deleted,
 * along with any files and directories it contains. It is your
 * responsibility to ensure that no further file system operations are
 * attempted inside the temporary directory once it has been deleted.
 *
 * ### Resource Leaking
 *
 * Various platform-specific conditions may cause [TempDir] to fail
 * to delete the underlying directory. Call [close] explicitly when
 * you need cleanup to happen and need to observe its result.
 *
 * Note that if the program exits before [close] is called, such as via
 * `exitProcess`, by segfaulting, or by receiving a signal like `SIGINT`,
 * then the temporary directory will not be deleted.
 */
class TempDir internal constructor(
    private var pathValue: String,
    /**
     * When `true`, [close] is a no-op (the on-disk directory is preserved).
     * Mirrors upstream disable cleanup flag.
     */
    var disableCleanup: Boolean,
) {
    /** Accesses the path to the temporary directory. */
    fun path(): String = pathValue

    /** Accesses the path to the temporary directory, matching upstream `AsRef<Path>`. */
    fun asRef(): String = path()

    /** Accesses the path to the temporary directory. */
    fun asPath(): String = path()

    /**
     * Disable cleanup of the temporary directory. If `disableCleanup` is `true`,
     * the temporary directory will not be deleted when this [TempDir] is closed.
     */
    fun disableCleanup(disableCleanup: Boolean) {
        this.disableCleanup = disableCleanup
    }

    /**
     * Persist the temporary directory to disk, returning the path where
     * it is located.
     *
     * This consumes the [TempDir] without deleting the directory on the
     * filesystem, meaning that the directory will no longer be automatically
     * deleted by a subsequent [close].
     */
    fun keep(): String {
        disableCleanup = true
        val taken = pathValue
        pathValue = ""
        return taken
    }

    /**
     * Consumes the temporary directory and returns its path without deleting it.
     *
     * Deprecated alias for [keep] matching upstream intoPath.
     */
    @Deprecated("use keep() instead", ReplaceWith("keep()"))
    fun intoPath(): String = keep()

    /**
     * Closes and removes the temporary directory, returning a [Result].
     *
     * Unlike the upstream `Drop` impl, this Kotlin port does not silently
     * swallow errors. The caller observes the [Result.failure] when the
     * recursive removal fails.
     */
    fun close(): Result<Unit> {
        if (pathValue.isEmpty()) return Result.success(Unit)
        val target = pathValue
        pathValue = ""
        if (disableCleanup) return Result.success(Unit)
        return removeDirAll(target)
    }

    /**
     * Drop implementation matching upstream Rust `Drop::drop`.
     */
    fun drop(): Result<Unit> = close()

    /**
     * Formats this [TempDir] matching upstream `Display::fmt`.
     */
    fun fmt(): String = toString()

    override fun toString(): String = "TempDir(path=${path()})"

    companion object {
        /**
         * Attempts to make a temporary directory inside the platform's
         * default temporary directory.
         */
        fun new(): Result<TempDir> = Builder().tempdir()

        /** Attempts to make a temporary directory inside [dir]. */
        fun newIn(dir: String): Result<TempDir> = Builder().tempdirIn(dir)

        /**
         * Creates a temporary directory at [path] with optional permissions and cleanup settings.
         */
        fun create(
            path: String,
            permissions: Int? = null,
            disableCleanup: Boolean = false,
        ): Result<TempDir> =
            io.github.kotlinmania.tempfile.dir.imp.create(path, permissions, disableCleanup)

        /**
         * Attempts to make a temporary directory with the specified
         * filename [prefix] inside the platform's default temporary
         * directory.
         */
        fun withPrefix(prefix: String): Result<TempDir> =
            Builder().prefix(prefix).tempdir()

        /**
         * Attempts to make a temporary directory with the specified
         * filename [suffix] inside the platform's default temporary
         * directory.
         */
        fun withSuffix(suffix: String): Result<TempDir> =
            Builder().suffix(suffix).tempdir()

        /**
         * Attempts to make a temporary directory with the specified
         * filename [prefix] inside [dir].
         */
        fun withPrefixIn(prefix: String, dir: String): Result<TempDir> =
            Builder().prefix(prefix).tempdirIn(dir)

        /**
         * Attempts to make a temporary directory with the specified
         * filename [suffix] inside [dir].
         */
        fun withSuffixIn(suffix: String, dir: String): Result<TempDir> =
            Builder().suffix(suffix).tempdirIn(dir)

        /**
         * Internal escape hatch used by [Builder.tempdirIn] to publish a
         * just-created on-disk directory into a [TempDir] without invoking
         * the public factories (which would re-enter the create path).
         */
        internal fun fromCreatedPath(path: String, disableCleanup: Boolean): TempDir =
            TempDir(pathValue = path, disableCleanup = disableCleanup)
    }
}
