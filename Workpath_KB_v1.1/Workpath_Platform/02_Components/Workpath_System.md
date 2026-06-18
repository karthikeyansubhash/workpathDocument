# Workpath System (`System-dune.apk`)

## Overview

| Property | Value |
|---|---|
| **Repository** | `System-dune-master` |
| **Package** | `com.hp.jetadvantage.link.system` |
| **Shared UID** | `android.uid.system` |
| **Version** | `0.00.135-s.18+D20260311` (versionCode 135) |
| **Build** | compileSdk 34, minSdk 26, targetSdk 31, AGP 8.2.2 |
| **APK** | `System-dune.apk` (pre-built included in repo) |
| **Module** | Single module `:app` |

Workpath System is the first platform component to initialize after Android boot. It manages the E2 WebSocket connection, device configuration synchronization, screen switching between Modern UI and Android, and launcher management.

**Rationale:** For Workpath apps to operate on the Dune platform, a central manager is needed to detect and synchronize device state changes (network, mode, time, locale, etc.) in real-time. Workpath System runs with `android.uid.system` privilege and prepares the device environment before other platform components (Services, Package Manager, etc.) initialize.

**Key Roles:**
1. **E2 WebSocket Connection**: Receives real-time events from Dune firmware via `Transport` / `E2WebSocektListener`
2. **WebSocket Callback Routing**: Four `WSCallback*` services classify and process incoming messages by type
3. **Device Configuration Sync**: Time (`TimeModel`), locale (`LocaleManager`), network (`NetworkManager`), certificates (`CertificateModel`), etc.
4. **Screen Switching**: Modern UI ↔ Android screen transition (`SwitchReceiver`)
5. **License Management**: Per-app license verification via `LicenseManager`
6. **Boot Sequence Initiation**: `BootCompletedReceiver` kicks off the entire platform initialization

## Build Variants

| Variant | Keystore | Notes |
|---|---|---|
| `debug` | `platform.keystore` | Debug logs enabled, ProGuard disabled |
| `release` | `platform.keystore` | Debug logs disabled, ProGuard enabled |
| `debug_sim` | `platform.jks` | Simulator variant |
| `release_sim` | `platform.jks` | Simulator variant, ProGuarded |

All variants have `enableTestReceiver: "true"`.

## Manifest Components

### Services (8)

| Service | Notes |
|---|---|
| `SystemService` | Core service, protected by `SYSTEM_PERMISSION` |
| `NLService` | `NotificationListenerService` |
| `WebSocketCallbackService` | WebSocket callback routing |
| `WSCallbackSystemManagement` | System management WebSocket callbacks |
| `WSCallbackCdmPubMsg` | CDM PubSub message callbacks |
| `WSCallbackGateway` | Gateway WebSocket callbacks |
| `WSCallbackStatusCheck` | Status check callbacks |
| `SecretKeyService` | Runs in `:WebSocketClient` process |

`BaseWebsocketCallbackService` is the base class for all `WSCallback*` services.

### Receivers (10)

| Receiver | Intent Filter | Notes |
|---|---|---|
| `BootCompletedReceiver` | `BOOT_COMPLETED`, `TEST_NOTI` | Entry point for platform boot |
| `SwitchReceiver` | `SWITCH` | Protected by `SWITCH_RECEIVER` permission |
| `CDMCallReceiver` | `CALL_WAKELOCK_CDM`, `CALL_GATEWAY_CLOSE`, `FORCE_REMOVE_TASK`, `CHECK_PROXY_INFO`, `CALL_DEVICE_READY`, `TEST_SYSTEM_STATE_CHANGE` | Multi-purpose CDM action handler |
| `ServiceInitCompleteReceiver` | `WORKPATH_SERVICE_INIT_COMPLETED` | |
| `ErrorCodeReceiver` | `link.errorcode` | |
| `ModeReceiver` | `EDX_CHANGED`, `AWAKE_CHANGED`, `DEV_TEST` | Device mode changes |
| `NotiReceiver` | `link.notification` | |
| `TestReceiver` | `DEVICE_READY`, `WORKPATH_SERVICE_READY` | |
| `TouchEventReceiver` | `USER_ACTIVITY` | Session timeout tracking |
| `WorkpathReadyBroadcastReceiver` | `READY_ACCESSLET`, `READY_STATISTICSLET`, `READY_STORAGELET`, `READY_DEVICEEVENTLET`, `READY_ACCESSORYLET` | Individual Let readiness |

### Provider (1)

| Provider | Authority | Permission |
|---|---|---|
| `SystemProvider` | `com.hp.workpath.system` | `SYSTEM_PERMISSION` |

### Activity (1)

- `DialogActivity`

## Key Source Classes

### Core Services
- **`SystemService`** — Central service managing device state, constant `WORKPATH_PLATFORM_VERSION = "31.8"`
- **`Transport`** / **`E2WebSocektListener`** — E2 WebSocket connection management

### Data & Configuration
- **`SystemProvider`** / **`SystemDBHelper`** — System data persistence
- **`HomeScreenModel`** — Launcher configuration
- **`LicenseManager`** / `LicenseModel`, `ActiveLicenseModel` — License enforcement
- **`LocaleManager`** / `LocaleModel` — Locale synchronization
- **`TimeModel`** / `TimeDataModel` — Time synchronization
- **`ModeChange`** — Device mode management
- **`SharedPreference`** — Persistent configuration
- **`CertificateModel`** — Certificate management
- **`TLSConfigModel`** — TLS configuration

### Network & CDM
- **`CDMDataManager`** — CDM data operations
- **`NetworkManager`** — Network/proxy management
- **`NsssService`** — NSSS operations

### WebSocket Data Models
- `CdmPubMessage`, `GatewayMessage`, `StatusCheckMessage`, `SystemManagementMessage`
- `WebsocketCallbackData`, `WorkpathGatewayData`, `SystemEventData`

### CDM System Models
- `Configuration`, `Identity`, `Image`, `Images`, `InstallationDate`, `MakeAndModel`, `ServiceConfig`, `Statistics`, `Status`
- Network models (33 classes): `AccountLockout`, `AirPrint`, `Authentication`, `Bonjour`, `ESCL`, `HttpProxy`, `IPP`, `SNMP`, etc.

### Utility
- **`AppLauncher`** — Launcher management
- **`AppStatusMonitor`** — App status tracking
- **`Constants`** — System-wide constants
- **`TaskRemover`** — Task management
- **`NotificationChannelManager`** — Notification channels
- **`StandardJsonParser`** — JSON parsing utility

## Permissions

### Defined by Workpath System

| Permission | Protection Level |
|---|---|
| `com.hp.jetadvantage.link.system.SYSTEM_PERMISSION` | `signatureOrSystem` |
| `com.hp.jetadvantage.link.system.SWITCH_RECEIVER` | `signatureOrSystem` |
| `com.hp.workpath.system.ENABLED_PACKAGES_PERMISSION` | `signatureOrSystem` |

### Used by Workpath System

`WRITE_EXTERNAL_STORAGE`, `INTERNET`, `RECEIVE_BOOT_COMPLETED`, `CHANGE_CONFIGURATION`, `REMOVE_TASKS`, `SET_TIME`, `SET_TIME_ZONE`, `WRITE_SETTINGS`, `READ_SETTINGS`, `WRITE_SECURE_SETTINGS`, `WAKE_LOCK`, `GET_TASKS`, `SET_KEYBOARD_LAYOUT`, `REORDER_TASKS`, `BIND_NOTIFICATION_LISTENER_SERVICE`, `FOREGROUND_SERVICE`, `SYSTEM_ALERT_WINDOW`, plus `LIST_PACKAGES` and `PACKAGE_LIFECYCLE_EVENTS` from Package Manager, `READ_WRITE_PROVIDERS` from datacollector.

## Protected Broadcasts

| Broadcast | Purpose |
|---|---|
| `android.intent.action.STATUS_BAR_CLOCK_CHANGE` | Clock change |
| `com.hp.jetadvantage.link.receivers.ACTION_POST` | Post action |
| `com.hp.jetadvantage.link.SWITCH` | Screen switch |
| `link.appinfos.runtime` | App runtime info |
| `com.hp.jetadvantage.link.intent.action.CALL_WAKELOCK_CDM` | CDM wake lock |
| `com.hp.jetadvantage.link.intent.action.CALL_GATEWAY_CLOSE` | Gateway close |
| `com.hp.jetadvantage.link.intent.action.FORCE_REMOVE_TASK` | Force remove task |
| `com.hp.workpath.system.DEVICE_READY` | Device ready |
| `com.hp.workpath.system.WORKPATH_SERVICE_READY` | Workpath Services ready |

## Dependencies

OkHttp 5.3.0, jdeferred 1.0.0, Guava 18.0, Jackson 2.13.5, Gson 2.13.2, AndroidX Security-Crypto 1.1.0-alpha03

## Signing

Platform-signed with `platform.pk8` / `platform.x509.pem`. Signing scripts included: `sign_system.bat`, `sign_system_release.bat`. Pre-built `System.apk` included in `System/` directory.
