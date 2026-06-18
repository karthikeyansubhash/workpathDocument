# Documentation Guide

> **Audience**: Workpath SDK developers
> **Version**: HP Workpath SDK v1.6.3

---

## 1. Overview

The SDK package includes three types of documentation:

| Document Type | Format | Path | Count |
|--------------|--------|------|-------|
| SDK User Guide | PDF | `Documentations/` | 1 |
| Feature Overview | PDF | `Documentations/` | 21 |
| API Reference | HTML + JAR | `APIDocs/` | 34 packages |

---

## 2. SDK User Guide

| File | Description |
|------|-------------|
| `HP WorkpathSDK-forDevice-SDKUserGuide_v1.6.3.pdf` | Comprehensive SDK usage guide covering setup, architecture, and development |

The SDK User Guide is the **primary document** that ISVs should read when first encountering the SDK. It covers:
- SDK installation and setup
- Project structure
- WorkpathLib.aar integration methods
- Basic app architecture
- Build and deployment process
- Simulator usage
- HPK packaging

---

## 3. Feature Overview Documents

21 PDF documents that explain each feature area's overview and sample usage.

### 3.1 Core Job Features

| File | Feature |
|------|---------|
| `HP WorkpathSDK-forDevice-ScanSampleOverview_v1.6.3.pdf` | Scan feature overview and ScanSample guide |
| `HP WorkpathSDK-forDevice-ScanOptions_v1.6.3.pdf` | Scan options detail (destination, format, resolution, etc.) |
| `HP WorkpathSDK-forDevice-PrintSampleOverview_v1.6.3.pdf` | Print feature overview and PrintSample guide |
| `HP WorkpathSDK-forDevice-PrintOptions_v1.6.3.pdf` | Print options detail (duplex, paper, quality, etc.) |
| `HP WorkpathSDK-forDevice-CopySampleOverview_v1.6.3.pdf` | Copy feature overview and CopySample guide |
| `HP WorkpathSDK-forDevice-EmailSampleOverview_v1.6.3.pdf` | Email feature overview and EmailSample guide |

### 3.2 Device & Access Features

| File | Feature |
|------|---------|
| `HP WorkpathSDK-forDevice-AccessSampleOverview_v1.6.3.pdf` | User access information retrieval |
| `HP WorkpathSDK-forDevice-AuthenticationAgentOverview_v1.6.3.pdf` | Custom authentication agent implementation |
| `HP WorkpathSDK-forDevice-AuthorizationSampleOverview_v1.6.3.pdf` | Permission management |
| `HP WorkpathSDK-forDevice-AttestationOverview_v1.6.3.pdf` | App token verification (Attestation) |
| `HP WorkpathSDK-forDevice-DeviceInfoSampleOverview_v1.6.3.pdf` | Device information retrieval |
| `HP WorkpathSDK-forDevice-DeviceEventsSampleOverview_v1.6.3.pdf` | Device event monitoring |
| `HP WorkpathSDK-forDevice-DeviceUsageSampleOverview_v1.6.3.pdf` | Device usage statistics |

### 3.3 Service & Utility Features

| File | Feature |
|------|---------|
| `HP WorkpathSDK-forDevice-ConfigSampleOverview_v1.6.3.pdf` | App configuration management |
| `HP WorkpathSDK-forDevice-HomeScreenOverview_v1.6.3.pdf` | Home screen (Launcher) integration |
| `HP WorkpathSDK-forDevice-MassStorageOverview_v1.6.3.pdf` | USB mass storage access |
| `HP WorkpathSDK-forDevice-StatisticsSampleOverview_v1.6.3.pdf` | Statistics tracking and retrieval |
| `HP WorkpathSDK-forDevice-SuppliesSampleOverview_v1.6.3.pdf` | Supply status retrieval |
| `HP WorkpathSDK-forDevice-WebServiceSampleOverview_v1.6.3.pdf` | Custom web service endpoints |
| `HP WorkpathSDK-forDevice-EventNotificationSampleOverview_v1.6.3.pdf` | System event broadcast reception |

### 3.4 Accessory Feature

| File | Feature |
|------|---------|
| `HP WorkpathSDK-forDevice-AccessoryOverview_v1.6.3.pdf` | USB HID accessory integration |

---

## 4. API Reference (Javadoc)

### 4.1 Structure

```
APIDocs/
├── WorkpathLib-javadoc.jar          ← For IDE integration (source attachment in Android Studio)
└── WorkpathLib-javadoc/             ← For browser viewing
    ├── index.html                   ← Main frame page
    ├── overview-summary.html        ← Package listing
    ├── allclasses-frame.html        ← All classes listing
    ├── deprecated-list.html         ← Deprecated API listing
    └── com/hp/workpath/api/         ← Per-package documentation
        ├── package-summary.html
        ├── Workpath.html
        ├── scanner/
        ├── printer/
        ├── copier/
        └── ...
```

### 4.2 Javadoc Generation

Javadoc is generated using `javadoc (1.8.0_292)`:

| Output | Format | Purpose |
|--------|--------|---------|
| HTML directory | HTML/CSS/JS | Web browser viewing |
| JAR file | JAR | Android Studio source attachment |

### 4.3 Coverage

Scope covered by Javadoc:

| Scope | Coverage |
|-------|----------|
| 34 packages | Complete |
| 150+ public classes | Complete |
| 100+ enums | Complete |
| Deprecated APIs | Separate listing (`deprecated-list.html`) |
| Since tags | API Level specified (e.g., "Since API 5") |

---

## 5. Documentation → Sample → API Mapping

Each Feature Overview PDF maps to a corresponding sample app and API package:

| PDF | Sample App | API Package |
|-----|-----------|-------------|
| ScanSampleOverview | ScanSample | `com.hp.workpath.api.scanner` |
| ScanOptions | ScanSample | `com.hp.workpath.api.scanner` (enums/attrs) |
| PrintSampleOverview | PrintSample | `com.hp.workpath.api.printer` |
| PrintOptions | PrintSample | `com.hp.workpath.api.printer` (enums/attrs) |
| CopySampleOverview | CopySample | `com.hp.workpath.api.copier` |
| EmailSampleOverview | EmailSample | `com.hp.workpath.api.helper.email` |
| AccessSampleOverview | AccessSample | `com.hp.workpath.api.access` |
| AuthenticationAgentOverview | AuthenticationAgent | `com.hp.workpath.api.access` |
| AuthorizationSampleOverview | AuthorizationSample | `com.hp.workpath.api.authorization` |
| AttestationOverview | AttestationSample | `com.hp.workpath.api.attestation` |
| DeviceInfoSampleOverview | DeviceInfoSample | `com.hp.workpath.api.device` |
| DeviceEventsSampleOverview | DeviceEventSample | `com.hp.workpath.api.device.events` |
| DeviceUsageSampleOverview | DeviceUsageSample | `com.hp.workpath.api.deviceusage` |
| ConfigSampleOverview | ConfigSample | `com.hp.workpath.api.config` |
| HomeScreenOverview | LauncherSample | `com.hp.workpath.api.launcher` |
| MassStorageOverview | MassStorageSample | `com.hp.workpath.api.massstorage` |
| StatisticsSampleOverview | StatisticsSample | `com.hp.workpath.api.statistics` |
| SuppliesSampleOverview | SuppliesSample | `com.hp.workpath.api.supplies` |
| WebServiceSampleOverview | WebServiceSample | `com.hp.workpath.api.webservices` |
| EventNotificationSampleOverview | EventNotificationSample | `Workpath.actions` (broadcasts) |
| AccessoryOverview | AccessorySample | `com.hp.workpath.api.accessory.hid` |

---

## 6. SDK Developer Checklist (Documentation)

### 6.1 At Release Time

- [ ] **Regenerate Javadoc** — Verify Javadoc comments on all public APIs
- [ ] **Deprecated tags** — Add `@deprecated` + replacement API guidance for deprecated APIs
- [ ] **Since tags** — Add `@since API <level>` to new APIs
- [ ] **Feature Overview PDFs** — Update overviews for changed features
- [ ] **SDK User Guide** — Reflect major changes
- [ ] **Release Notes** — Document additions, changes, and removals
- [ ] **HPK Tool User Guide** — Update when tool changes
- [ ] **Simulator User Guide** — Update when Simulator changes
- [ ] **Bulk version number update** — Update version numbers in all PDF filenames and content

### 6.2 Naming Convention Compliance

```
HP WorkpathSDK-<scope>-<name>_v<major>.<minor>.<patch>.pdf
```

| scope | Usage |
|-------|-------|
| `forDevice` | Device SDK documents |
| `Simulator` | Simulator documents |
| `HPKTool` | Tool documents |
| `Releasenote` | Release notes |

---

*← [00_Index](../00_Index.md)*
