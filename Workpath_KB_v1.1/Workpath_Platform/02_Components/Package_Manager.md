# Package Manager — Dune Platform

## Overview

| Property | Value |
|---|---|
| **Repository** | `packagemanager-dune-master` |
| **Application** | `SmartUXPackageManager` |
| **Package** | `com.hp.jetadvantage.link.packagemanager` |
| **Shared UID** | `android.uid.system` |
| **Version** | `1.6.2-s.14+D20251201` (versionCode 42) |
| **Build** | compileSdk 31, minSdk 29, targetSdk 29, AGP 8.2.2 |

Package Manager handles HPK package installation, signature verification, and solution app lifecycle on Dune devices.

**Rationale:** Workpath apps are distributed as HPK (HP Package) files rather than standard Android APKs. HPK wraps an APK with HP signatures, requiring signature verification, solution registration, and permission management per HP security policy. Package Manager handles the entire HPK lifecycle.

**Key Roles:**
1. **HPK Install/Uninstall**: `PreInstaller` → APK extraction + signature verification → `PostInstaller` → solution registration
2. **Signature Verification**: Uses CpkLib to verify HP signature integrity of HPK packages
3. **Solution Registry**: 5 ContentProviders expose installed app list, install queue, providers, and attestation info to other components
4. **App State Notification**: Broadcasts install/uninstall events to other platform components
5. **WebSocket Callback**: Receives package-related events from Dune firmware via `DuneCallbackService`

> **Platform note** (from README): Package Manager requires access to internal `PackageManager` API not available in the standard `android.jar`. Compilation requires a custom `android.jar` with updated `android.content.pm` package (classes taken from UI 4G `framework.jar`). Copy and overwrite the `platform` folder to the `<ANDROID_SDK>` folder.

## Module Structure

| Module | Directory | Purpose |
|---|---|---|
| `:Apps:SmartUXPackageManager` | `Apps/SmartUXPackageManager` | Application APK |
| `:Libs:PackageManager` | `Libs/PackageManager` | Core library |
| `:Libs:CpkLib` | `Libs/CpkLib` | HPK packaging library |
| `:Libs:oxpd2` | `Libs/oxpd2` | E2 client library |
| `:Test` | `tests` | Test module |

**Additional directories** (not in main settings.gradle):
- `Apps/HpkTool` — Standalone Gradle project wrapping CpkLib + cpktool. Outputs `HPKTool_linux.zip` and `HPKTool_win.zip`.
- `Apps/cpktool` — CLI HPK packaging tool. Contains `hpktool.sh`, `hpktool_for_web.sh`, `make_for_web.sh`.
- `Libs/DeviceConnect`, `Libs/DeviceConnectLib`, `Libs/LinkDataCollectorLib`, `Libs/ext`

## Build Variants

| Variant | Keystore | Notes |
|---|---|---|
| `debug` | `platform.keystore` | |
| `release` | `platform.keystore` | ProGuard enabled |
| `debugForSim` | `emulator.keystore` | Adds `android.uid.system` |
| `releaseForSim` | `emulator.keystore` | ProGuard enabled |

## Manifest Components

### Application
- Class: `PackageManagerApplication` (runs in `:PackageManager` process)

### ContentProviders (5)

| Provider | Authority | Permission |
|---|---|---|
| `PackageContentProvider` | `packages` | `LIST_PACKAGES` |
| `PackageInstallerContentProvider` | `packages-installers` | `LIST_PACKAGES` |
| `PackageProvidersProvider` | `providers` | `READ_PROVIDERS` |
| `PackageAttestationContentProvider` | `packages-attestation` | `READ_WRITE_ATTESTATION` |
| `PackageInstallerQueuesContentProvider` | `packages-installersqueue` | `LIST_PACKAGES` |

### Receivers (5)

| Receiver | Intent Filter | Notes |
|---|---|---|
| `DeviceReadyReceiver` | `DEVICE_READY` | Protected by `SYSTEM_PERMISSION` |
| `AppInstalledBroadcastReceiver` | `DUNE_PACKAGE_ADDED` | Package scheme |
| `AppUninstalledBroadcastReceiver` | `UNINSTALL` | Protected by `PACKAGE_LIFECYCLE_EVENTS` |
| `AttestationChangeBroadcastReceiver` | `ACTION_ATTESTATION` | |
| `NotificationChangeBroadcastReceiver` | `NOTIFICATION_CHANGED` | Protected by `READ_PROVIDERS` |

### Services (2)

| Service | Purpose |
|---|---|
| `DuneCallbackService` | WebSocket callback handling |
| `BootTasksService` | Boot initialization |

## Permissions

### Defined (9)

| Permission | Protection Level | Group |
|---|---|---|
| `com.hp.packagemanager.permission.LIST_PACKAGES` | `signatureOrSystem` | `PACKAGE_MANAGER` |
| `com.hp.packagemanager.permission.PACKAGE_LIFECYCLE_EVENTS` | `signatureOrSystem` | `PACKAGE_MANAGER` |
| `com.hp.packagemanager.permission.READ_PROVIDERS` | `signatureOrSystem` | `PACKAGE_MANAGER` |
| `com.hp.packagemanager.permission.READ_WRITE_CONFIG` | `signatureOrSystem` | `PACKAGE_MANAGER` |
| `com.hp.packagemanager.permission.READ_WRITE_SYSTEMCONFIG` | `signatureOrSystem` | `PACKAGE_MANAGER` |
| `com.hp.packagemanager.permission.READ_WRITE_AVATAR_REGISTRATION` | `signatureOrSystem` | `PACKAGE_MANAGER` |
| `com.hp.packagemanager.permission.READ_WRITE_ATTESTATION` | `signatureOrSystem` | `PACKAGE_MANAGER` |
| `com.hp.workpath.permission.ACCESS_CUSTOM_AUTH` | `normal` | `PACKAGE_MANAGER` |
| `android.permission.ACCOUNT_MANAGER` | `signature` | — |

### Used
`INTERNET`, `ACCESS_WIFI_STATE`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `INSTALL_PACKAGES`, `DELETE_PACKAGES`, `WRITE_SETTINGS`, `GRANT_RUNTIME_PERMISSIONS`, `SYSTEM_ALERT_WINDOW`, `FOREGROUND_SERVICE`, plus all its own defined permissions, `SYSTEM_PERMISSION`, `datacollector.READ_WRITE_PROVIDERS`, `ACCESS_STATISTICS_PERMISSION`, `svcmanager.ACCESS_PROVIDERS`.

## Key Source Classes

- **Core**: `PackageManager`, `PackageManagerApplication`, `PackageContract`, `Constants`
- **Providers**: `PackageContentProvider`, `PackageInstallerContentProvider`, `PackageProvidersProvider`, `PackageAttestationContentProvider`, `PackageInstallerQueuesContentProvider`, `PackageDBHelper`
- **Receivers**: `AppInstalledBroadcastReceiver`, `AppUninstalledBroadcastReceiver`, `AttestationChangeBroadcastReceiver`, `DeviceReadyReceiver`, `NotificationChangeBroadcastReceiver`
- **Services**: `BaseWebsocketCallbackService`, `BootTasksService`, `DuneCallbackService`
- **Install/Uninstall**: `PostInstaller`, `PreInstaller`
- **Sub-packages**: activities, builder, connect, controller, exception, helper, model, notification, uninstaller, utils

## HPK Tool

The HPK Tool converts APK files to HPK packages for deployment to HP devices.

- Source: `Apps/HpkTool` (standalone Gradle project) + `Apps/cpktool` + `Libs/CpkLib`
- Distribution: `HPKTool_win.zip`, `HPKTool_linux.zip`
- Scripts: `hpktool.sh`, `hpktool_for_web.sh`

## CDM Endpoints Used

From source code (`CDM.java`):
- `/ext/solutionManager/v1/solutions` — Solution management
