# Log Daemon — Dune Platform

## Overview

| Property | Value |
|---|---|
| **Repository** | `LogDaemon-dune-master` |
| **Package** | `com.hp.jetadvantage.link.logdaemon` |
| **Shared UID** | `android.uid.system` |
| **Version** | 1.00.20 (versionCode 20) |
| **Build** | compileSdk 31, minSdk 26, targetSdk 31, AGP 8.1.3 |
| **APK** | `LogDaemon-dune.apk` |
| **Module** | Single module `:app` |

Log Daemon is a system service that collects and persists Android logcat output for diagnostic retrieval via EWS or USB.

**Rationale:** MFP devices are deployed in environments where developers have limited direct ADB access (customer sites, remote devices). To collect diagnostic logs when issues occur, logcat output must be continuously persisted and retrievable remotely via EWS (Embedded Web Server) or USB.

**Key Roles:**
1. **Logcat Collection**: `ReaderThread` continuously reads and stores Android logcat output in the background
2. **Log Persistence**: `LogCollection` persists collected logs to the file system
3. **Remote Retrieval**: Stored logs can be retrieved via EWS or USB
4. **Auto-start on Boot**: `BootCompletedReceiver` automatically starts `LogcatService` (Foreground Service) upon boot completion

## Build Variants

| Variant | Keystore |
|---|---|
| `debug` | `platform.keystore` |
| `release` | `platform.keystore` |
| `debug_sim` | `platform.jks` |
| `release_sim` | `platform.jks` |

## Manifest Components

### Permission Defined

| Permission | Protection Level |
|---|---|
| `com.hp.jetadvantage.link.system.LOGDAEMON_PERMISSION` | `signatureOrSystem` |

### Permissions Used

`INTERNET`, `RECEIVE_BOOT_COMPLETED`, `READ_LOGS`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `READ_INTERNAL_STORAGE`, `WRITE_INTERNAL_STORAGE`, `INSTALL_PACKAGES` (from packagemanager), `GET_TASKS`

### Receiver

| Receiver | Intent Filter |
|---|---|
| `BootCompletedReceiver` | `BOOT_COMPLETED`, `com.hp.intent.action.logdaemon.start` |

### Service

| Service | Notes |
|---|---|
| `LogcatService` | Foreground service, protected by `LOGDAEMON_PERMISSION` |

## Key Source Classes

- **`LogcatService`** — Main log collection foreground service
- **`LogCollection`** — Log collection logic
- **`LogModel`** — Data model for logs
- **`ReaderThread`** — Background log reading thread
- Sub-packages: data, receivers, transport

## Dependencies

constraint-layout 1.1.0, OkHttp 3.10.0, localbroadcastmanager 1.0.0, jdeferred 1.0.0, Guava 18.0, Jackson 2.6.7

## Signing

Platform-signed: `platform.keystore` (device), `platform.jks` (simulator). Same signing pattern as System and Services.
