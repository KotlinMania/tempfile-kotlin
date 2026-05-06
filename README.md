# tempfile-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Ftempfile--kotlin-blue.svg)](https://github.com/KotlinMania/tempfile-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/tempfile-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/tempfile-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/tempfile-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/tempfile-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`Stebalien/tempfile`](https://github.com/Stebalien/tempfile).

**Original Project:** This port is based on [`Stebalien/tempfile`](https://github.com/Stebalien/tempfile). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `Stebalien/tempfile`

> The text below is reproduced and lightly edited from [`https://github.com/Stebalien/tempfile`](https://github.com/Stebalien/tempfile). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## tempfile

[![Crate](https://img.shields.io/crates/v/tempfile.svg)](https://crates.io/crates/tempfile)
[![Build Status](https://github.com/Stebalien/tempfile/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/Stebalien/tempfile/actions/workflows/ci.yml?query=branch%3Amaster)

A secure, cross-platform, temporary file library for Rust. In addition to creating
temporary files, this library also allows users to securely open multiple
independent references to the same temporary file (useful for consumer/producer
patterns and surprisingly difficult to implement securely).

[Documentation](https://docs.rs/tempfile/)

## Usage

Minimum required Rust version: 1.63.0

Add this to your `Cargo.toml`:

```toml
[dependencies]
tempfile = "3"
```

## Supported Platforms

This crate supports all major operating systems:

- Linux
- Android
- MacOS
- Windows
- FreeBSD (likely other BSDs but we don't have CI for them)
- RedoxOS
- Wasm (build and link only, Wasm doesn't have a filesystem)
- WASI P1 & P2.

However:

- Android, RedoxOS, Wasm, and WASI targets all require the latest stable rust compiler.
- WASI P1/P2 does not define a default temporary directory. You'll need to explicitly call `tempfile::env::override_temp_dir` with a valid directory or temporary file creation will panic on this platform.
- WASI P1/P2 does not have file permissions.
- You _may_ need to override the temporary directory in Android as well to point at your application's per-app cache directory.

## Example

```rust
use std::fs::File;
use std::io::{Write, Read, Seek, SeekFrom};

fn main() {
    // Write
    let mut tmpfile: File = tempfile::tempfile().unwrap();
    write!(tmpfile, "Hello World!").unwrap();

    // Seek to start
    tmpfile.seek(SeekFrom::Start(0)).unwrap();

    // Read
    let mut buf = String::new();
    tmpfile.read_to_string(&mut buf).unwrap();
    assert_eq!("Hello World!", buf);
}
```

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:tempfile-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`Stebalien/tempfile`](https://github.com/Stebalien/tempfile). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the tempfile authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`Stebalien/tempfile`](https://github.com/Stebalien/tempfile) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
