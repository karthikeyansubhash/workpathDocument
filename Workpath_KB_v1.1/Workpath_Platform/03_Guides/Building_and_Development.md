# Building & Development Guide — Dune Platform

## 1. Prerequisites

| Tool | Version |
|---|---|
| JDK | 17 |
| Android Gradle Plugin | 8.2.2 (System, Services, PM) / 8.1.3 (LogDaemon) |

### Build Configuration by Repository

| Repository | compileSdk | minSdk | targetSdk |
|---|---|---|---|
| `System-dune-master` | 34 | 26 | 31 |
| `workpath-services-dune-master` | 31 | 29 | 29 |
| `packagemanager-dune-master` | 31 | 29 | 29 |
| `LogDaemon-dune-master` | 31 | 26 | 31 |

## 2. Build Commands

### Workpath Services
```bash
cd workpath-services-dune-master
./gradlew :App-WorkpathServices:assembleDebug          # Device debug
./gradlew :App-WorkpathServices:assembleRelease         # Device release
./gradlew :App-WorkpathServices:assembleDebugForSim     # Simulator debug
./gradlew :App-WorkpathServices:assembleReleaseForSim   # Simulator release
```

### Workpath System
```bash
cd System-dune-master/System
./gradlew :app:assembleDebug
./gradlew :app:assembleDebug_sim
```

### Package Manager
```bash
cd packagemanager-dune-master
./gradlew :Apps:SmartUXPackageManager:assembleDebug
./gradlew :Apps:SmartUXPackageManager:assembleDebugForSim
```

### Log Daemon
```bash
cd LogDaemon-dune-master
./gradlew :app:assembleDebug
```

## 3. Build Variants

### Workpath Services (4 variants)

| Variant | Debug | ProGuard | Keystore |
|---|---|---|---|
| `debug` | ✓ | ✗ | `platform.keystore` |
| `release` | ✗ | ✓ | `platform.keystore` |
| `debugForSim` | ✓ | ✗ | `emulator.keystore` |
| `releaseForSim` | ✗ | ✓ | `emulator.keystore` |

Sim variants add `android:sharedUserId="android.uid.system"`.

### Workpath System (4 variants)

| Variant | Debug | ProGuard | Keystore |
|---|---|---|---|
| `debug` | ✓ | ✗ | `platform.keystore` |
| `release` | ✗ | ✓ | `platform.keystore` |
| `debug_sim` | ✓ | ✗ | `platform.jks` |
| `release_sim` | ✗ | ✓ | `platform.jks` |

> **Note**: System uses `debug_sim`/`release_sim` naming (underscore), while Services uses `debugForSim`/`releaseForSim` (camelCase).

## 4. Signing Configuration

| Component | Device Keystore | Sim Keystore | Location |
|---|---|---|---|
| Workpath Services | `platform.keystore` | `emulator.keystore` | `ForBuild/keys/` |
| Workpath System | `platform.keystore` | `platform.jks` | `ForBuild/keys/` |
| Package Manager | `platform.keystore` | `emulator.keystore` | `Platform/` |
| Log Daemon | `platform.keystore` | `platform.jks` | `ForBuild/keys/` |

Common credentials (from `build.gradle`): password `"android"`, alias `"androiddebugkey"`.

System also has platform-level signing with `platform.pk8` / `platform.x509.pem` and signing scripts: `sign_system.bat`, `sign_system_release.bat`.

## 5. Local Development Workflow

```bash
# 1. Build debug variant
./gradlew :App-WorkpathServices:assembleDebug

# 2. Install to device/emulator
adb install -r WorkpathServices-dune.apk

# 3. Trigger device ready (required for initialization)
adb shell am broadcast \
  -a com.hp.workpath.intent.action.CALL_DEVICE_READY \
  -n com.hp.jetadvantage.link.system/.receivers.CDMCallReceiver

# 4. Verify via logs
adb logcat | grep SSS
```

## 6. Version Management

### Version Format
```
{API Version}-{branch}.{counter}+D{YYYYMMDD}
```

Examples:
- Services: `1.6.2-s.60+D20260219`
- System: `0.00.135-s.18+D20260311`
- Package Manager: `1.6.2-s.14+D20251201`

Version properties maintained in `version.properties` (Services, PM) with auto-update via CI.

### SDK Version Format
```
{Major}.{Minor}.{Patch} ({YYYYMMDD})
```
Example: `1.6.3 (20251111)`

## 7. CI/CD Pipeline

From Workpath Services README:
- **PI testing** on PR creation: build all variants → unit tests → instrumented tests → API tests
- Merge via `ready-to-merge` label
- Auto version increment on merge
- System-dune-master has `Jenkinsfile` for CI

## 8. Dependency Summary

### Key Dependency Differences Between Components

| Library | System | Services | PM | LogDaemon |
|---|---|---|---|---|
| **OkHttp** | 5.3.0 | 4.12.0 | — | 3.10.0 |
| **Jackson** | 2.13.5 | 2.8.8 | — | 2.6.7 |
| **Guava** | 18.0 | 31.1-android | — | 18.0 |
| **Gson** | 2.13.2 | 2.13.2 | — | — |

> **Important**: System and Services use different major versions of OkHttp and Jackson. Be aware of API differences when sharing code between components.

## 9. Package Manager Custom Platform

From Package Manager README:

> PackageManager Service requires access to internal PackageManager API not available in standard `android.jar`. To compile, use a custom `android.jar` with updated `android.content.pm` package (classes taken from UI 4G `framework.jar`). Copy and overwrite the `platform` folder to the `<ANDROID_SDK>` folder.

The `Platform/` directory contains: `platform.keystore`, `emulator.keystore`, `platform.pk8`/`.pem`, and a custom `platforms/` folder.

## 10. Development Flow

From the [Development Flow KB](../../Knowledge_source/Workpath%20Development%20Flow_KB/Development_flow.md):

### Process Phases
1. **New Requirement & Prioritization** — Customer → JIRA backlog → Scope selection → Management approval → Release plan
2. **Development** — Epic Review (design) → Story Break-down → Resource Assignment → Kickoff → Development → Qualification → Quality Review
3. **SDK Qualification** — Consolidate changes → Issue firmware + SDK package → Deliver to Qual team
4. **SDK Package Release** — Submit to Developer Site

### Development Targets (3 areas)

| Area | Components |
|---|---|
| SDK Package | Library (WorkpathLib.aar), Documentation, Sample Apps, HPK Tool, Simulator |
| Workpath Platform | Workpath Services, Workpath System |
| Device Firmware | E2 API, E2Interop, CDM |

### Release Deliverables

1. **WorkpathLib.aar** — Core SDK library
2. **API Documentation** — Javadoc
3. **Feature Overview Documents** — 22 PDFs
4. **Sample Applications** — 23 Java + 23 Kotlin
5. **Extension Samples** — GoogleSigninSample
6. **HPK Tool** — APK-to-HPK packaging (Windows/Linux)
7. **Simulator** — PC-based dev/test environment
8. **Release Notes**
