# Workpath SDK Lib (Client Library)

## Overview

| Property | Value |
|---|---|
| **Repository** | `linksdklib-master` |
| **Output** | `WorkpathLib.aar` |
| **Build** | compileSdk 26, minSdk 19, targetSdk 26, AGP 3.0.1 |
| **Version** | `1.6.3 (20251111)` (versionCode 29) |

WorkpathLib is the client SDK library used by third-party solution app developers to access Workpath platform features. It is distributed as an AAR file and works across firmware platforms (the same `.aar` runs on both JOLT and Dune).

**Rationale:** Solution app developers need a standardized API to access MFP hardware features such as scan, print, copy, and authentication. WorkpathLib abstracts the ContentProvider IPC mechanism so developers can use straightforward Java/Kotlin APIs without needing to know the underlying `ContentResolver.call()` invocation details.

**Key Roles:**
1. **API Abstraction**: Wraps ContentProvider-based IPC into Java/Kotlin APIs. e.g., `ScannerService.isSupported()` internally calls `ContentResolver.call("scanletcp", "IS_SUPPORTED", ...)`
2. **Authority/Method Definitions**: The `JetAdvantageLinkApi` module centrally defines all Let ContentProvider authorities, method names, and Bundle key constants
3. **Result Handling**: Unified error handling via the `Result` class and `Result.ErrorCode` enumeration
4. **Platform Independence**: The same AAR runs on both JOLT and Dune firmware (only the backend Let implementation differs)
5. **Sample Code**: 23 Java + 23 Kotlin sample apps demonstrating all API usage

## Module Structure

| Module | Directory | Purpose |
|---|---|---|
| `:Util-Common` | `Libs/Common` | Shared utilities |
| `:API-JetAdvantageLinkApi` | `Libs/JetAdvantageLinkApi` | API contracts (authorities, method names) |
| `:Build-JetAdvantageLinkInternalLibs` | `Libs/JetAdvantageLinkInternalLibs` | Internal library consolidation |
| `:Library-JetAdvantageLinkLib` | `Libs/JetAdvantageLinkLib` | Main library (outputs WorkpathLib.aar) |
| `:Test-WorkpathLibTests` | `WorkpathLibTests` | SDK tests |

## API Surface

### Entry Point
```java
Workpath.getInstance().initialize(context);
```

### API Packages

Under `com.hp.workpath.api.*`:

| Package | Feature |
|---|---|
| `access` | Device access control |
| `accessory` | USB accessory management |
| `attestation` | Device attestation |
| `authorization` | Authorization |
| `config` | Configuration |
| `copier` | Copy operations |
| `device` | Device info |
| `deviceusage` | Device usage data |
| `helper` | Utility helpers |
| `job` | Job management |
| `launcher` | Launcher integration |
| `massstorage` | USB mass storage |
| `printer` | Print operations |
| `scanner` | Scan operations |
| `statistics` | Print/scan statistics |
| `supplies` | Supply levels |
| `webservices` | Web service access |

Legacy package `com.hp.jetadvantage.link.api.*` is retained for backward compatibility.

### Top-Level Classes
- `Workpath` — SDK entry point, singleton
- `Workpath.actions` — Action constants
- `Workpath.permissions` — Permission constants
- `Result` — Operation result wrapper
- `Result.ErrorCode` — Error code enumeration
- `ILetObserver` — Let state observer interface
- `SsdkUnsupportedException` — Thrown when SDK service is unavailable
- `CapabilitiesExceededException` — Thrown when requested capability exceeds device limits

## Communication Mechanism

WorkpathLib communicates with Workpath Services via Android `ContentProvider.call()` IPC:

```
ScannerService.isSupported()
  → ContentResolver.call(
      authority = "com.hp.jetadvantage.link.authority.scanletcp",
      method = "IS_SUPPORTED",
      arg = null,
      extras = Bundle)
    → ScanletContentProvider.call()
```

The `JetAdvantageLinkApi` module defines:
- ContentProvider authority strings (one per Let)
- Method name constants (e.g., `IS_SUPPORTED`, `GET_CAPS`, `GET_DEFAULTS`)
- Bundle key constants for IPC data

> `ContentResolver.call()` is synchronous on the calling thread. Let ContentProviders use `StrictMode.ThreadPolicy.permitNetwork()` to allow network calls within the ContentProvider's `call()` method.

## SDK Distribution

The SDK is distributed as part of the **HP Workpath SDK Package** (v1.6.3):

```
HPWorkpath_v1.6.3/
├── APIDocs/
│   ├── WorkpathLib-javadoc/        # Javadoc HTML site
│   └── WorkpathLib-javadoc.jar
├── Documentations/                  # 22 feature overview PDFs
├── Libraries/
│   └── WorkpathLib.aar             # Core SDK library
├── Samples/
│   └── ExampleAPIServices/
│       ├── apks/                   # Pre-built sample APKs
│       ├── hpk/                    # Pre-built HPK packages
│       └── source/                 # 22 Java sample sources
├── Samples_Kotlin/
│   └── ExampleAPIServices/         # 22 Kotlin sample sources
├── Tools/
│   └── hpktool/                    # HPKTool_linux.zip, HPKTool_win.zip
└── HP WorkpathSDK-Releasenote-1.6.3.pdf
```

### Sample Applications (23 per language)

Java samples (`linksdk_next_samples-master`) and Kotlin samples (`linksdk_next_kotlin-master`):
AccessSample, AccessorySample, AccessoryAgentSample, AccessoryServiceSample, AttestationSample, AuthenticationAgent, AuthenticationAgentWithPrePrompt, AuthorizationSample, ConfigSample, CopySample, DeviceEventSample, DeviceInfoSample, DeviceUsageSample, EmailSample, EventNotificationSample, LauncherSample, MassStorageSample, MultiLanguageSample, PrintSample, ScanSample, StatisticsSample, SuppliesSample, WebServiceSample

Extension: GoogleSigninSample

### Feature Documentation PDFs (22)

AccessoryOverview, AccessSampleOverview, AttestationOverview, AuthenticationAgentOverview, AuthorizationSampleOverview, ConfigSampleOverview, CopySampleOverview, DeviceEventsSampleOverview, DeviceInfoSampleOverview, DeviceUsageSampleOverview, EmailSampleOverview, EventNotificationSampleOverview, HomeScreenOverview, MassStorageOverview, PrintOptions, PrintSampleOverview, ScanOptions, ScanSampleOverview, SDKUserGuide, StatisticsSampleOverview, SuppliesSampleOverview, WebServiceSampleOverview

## Sample App Build Configuration

| Property | Value |
|---|---|
| compileSdk | 31 |
| minSdk | 31 |
| targetSdk | 31 |
| AGP | 7.4.2 |
| Kotlin (kotlin samples) | 1.8.20 |

Additional dependencies in Kotlin samples: Kotlin Coroutines 1.7.2, Lifecycle-Runtime-KTX 2.5.1, Fragment-KTX 1.5.5, Gson 2.11.0
