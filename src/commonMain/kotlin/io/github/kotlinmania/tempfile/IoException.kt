// port-lint: ignore
// Kotlin-side analog of Rust's `std::io::Error` and `std::io::ErrorKind`. The
// upstream tempfile crate relies on the standard-library types directly; the
// Kotlin port lives in a commonMain world that has no built-in IO error type
// and therefore models the same kind discriminator plus optional payload chain
// here.
package io.github.kotlinmania.tempfile

internal enum class IoErrorKind {
    NotFound,
    PermissionDenied,
    ConnectionRefused,
    ConnectionReset,
    HostUnreachable,
    NetworkUnreachable,
    ConnectionAborted,
    NotConnected,
    AddrInUse,
    AddrNotAvailable,
    NetworkDown,
    BrokenPipe,
    AlreadyExists,
    WouldBlock,
    NotADirectory,
    IsADirectory,
    DirectoryNotEmpty,
    ReadOnlyFilesystem,
    FilesystemLoop,
    StaleNetworkFileHandle,
    InvalidInput,
    InvalidData,
    TimedOut,
    WriteZero,
    StorageFull,
    NotSeekable,
    FileTooLarge,
    ResourceBusy,
    ExecutableFileBusy,
    Deadlock,
    CrossesDevices,
    TooManyLinks,
    InvalidFilename,
    ArgumentListTooLong,
    Interrupted,
    Unsupported,
    UnexpectedEof,
    OutOfMemory,
    Other,
}

internal open class IoException : RuntimeException {
    val kind: IoErrorKind
    private val payloadString: String?

    constructor(kind: IoErrorKind, message: String) : super(message) {
        this.kind = kind
        this.payloadString = null
    }

    constructor(kind: IoErrorKind, payload: Throwable) : super(payload.message, payload.cause) {
        this.kind = kind
        this.payloadString = payload.toString()
    }

    override fun toString(): String = payloadString ?: (message ?: kind.name)
}
