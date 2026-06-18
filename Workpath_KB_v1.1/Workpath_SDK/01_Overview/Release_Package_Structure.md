# Release Package Structure

> **Audience**: Workpath SDK developers
> **Version**: HP Workpath SDK v1.6.3

---

## 1. Package Directory Tree

```
HPWorkpath_v1.6.3/
├── HP WorkpathSDK-Releasenote-1.6.3.pdf          ← Release notes
├── APIDocs/
│   ├── WorkpathLib-javadoc.jar                    ← Javadoc JAR archive
│   └── WorkpathLib-javadoc/                       ← Javadoc HTML (browser)
│       ├── index.html                             ← Javadoc main page
│       ├── overview-summary.html                  ← Package listing
│       ├── allclasses-frame.html
│       └── com/hp/workpath/api/...               ← Per-package docs (34 packages)
├── Documentations/
│   ├── HP WorkpathSDK-forDevice-SDKUserGuide_v1.6.3.pdf
│   ├── HP WorkpathSDK-forDevice-ScanSampleOverview_v1.6.3.pdf
│   ├── HP WorkpathSDK-forDevice-PrintSampleOverview_v1.6.3.pdf
│   ├── HP WorkpathSDK-forDevice-CopySampleOverview_v1.6.3.pdf
│   └── ... (22 PDFs total)
├── Libraries/
│   ├── WorkpathLib.aar                            ← Core SDK library
│   └── WorkpathSDK_Opensource_Announcement.txt     ← Open source notice
├── Samples/
│   ├── ExampleAPIServices/
│   │   ├── apks/        ← 23 prebuilt APKs
│   │   ├── hpk/         ← 23 prebuilt HPKs
│   │   └── source/      ← Java source project
│   │       ├── build.gradle
│   │       ├── settings.gradle
│   │       ├── WorkpathLib/    ← AAR wrapper module
│   │       ├── ScanSample/
│   │       ├── PrintSample/
│   │       └── ... (23 app modules)
│   ├── ExampleExtensions/
│   │   └── source/
│   │       ├── GoogleSigninSample/
│   │       └── ...
│   └── THIRD-PARTY-LICENSES.txt
├── Samples_Kotlin/
│   ├── ExampleAPIServices/
│   │   ├── apks/        ← 23 prebuilt APKs
│   │   ├── hpk/         ← 23 prebuilt HPKs
│   │   └── source/      ← Kotlin source project
│   │       ├── build.gradle
│   │       ├── settings.gradle
│   │       ├── WorkpathLib/
│   │       └── ... (23 app modules)
│   ├── ExampleExtensions/
│   │   └── source/
│   │       └── GoogleSigninSample/
│   └── THIRD-PARTY-LICENSES.txt
└── Tools/
    ├── HP WorkpathSDK-HPKTool-UserGuide_v1.6.3.pdf
    └── hpktool/
        ├── HPKTool_win.zip
        └── HPKTool_linux.zip
```

**Separately distributed — Simulator:**
```
HPWorkpathSDK_Simulator_v1.6.3/
├── HP WorkpathSDK-Simulator-UserGuide_v1.6.3.pdf
├── SetupWorkpathSDKSimulator_v1.6.3.exe           ← Windows installer
└── THIRD-PARTY-LICENSES.txt
```

---

## 2. Component Summary

| Component | Path | Format | Count | Description |
|-----------|------|--------|-------|-------------|
| Release Notes | `HP WorkpathSDK-Releasenote-1.6.3.pdf` | PDF | 1 | Per-version change log |
| API Docs | `APIDocs/WorkpathLib-javadoc/` | HTML + JAR | 34 packages | Javadoc API reference |
| Feature Docs | `Documentations/` | PDF | 22 | 1 SDK User Guide + 21 per-feature overviews |
| Library | `Libraries/WorkpathLib.aar` | AAR | 1 | Core SDK library |
| Java Samples | `Samples/ExampleAPIServices/source/` | Gradle Project | 23 apps | Java reference implementation |
| Java Extensions | `Samples/ExampleExtensions/source/` | Gradle Project | 1 app | Extension integration sample |
| Kotlin Samples | `Samples_Kotlin/ExampleAPIServices/source/` | Gradle Project | 23 apps | Kotlin reference implementation |
| Kotlin Extensions | `Samples_Kotlin/ExampleExtensions/source/` | Gradle Project | 1 app | Extension integration sample |
| Prebuilt APKs | `*/apks/` | APK | 46 (23×2) | Ready-to-install APKs |
| Prebuilt HPKs | `*/hpk/` | HPK | 46 (23×2) | Device-installable HPKs |
| HPK Tool | `Tools/hpktool/` | ZIP | 2 (Win+Linux) | APK-to-HPK conversion tool |
| Simulator | (separate package) | EXE | 1 | PC simulator |

---

## 3. Version Naming Convention

```
HP WorkpathSDK-<scope>-<name>_v<major>.<minor>.<patch>.pdf
```

- **scope**: `forDevice` (device docs), `Simulator` (simulator docs), `HPKTool` (tool docs)
- **name**: document/feature name (e.g., `ScanSampleOverview`, `SDKUserGuide`)
- **version**: semantic versioning (`1.6.3`)

Sample app versionName: `1.6.3 (20251111)` — build identifier including date

---

## 4. Deliverables Checklist

Checklist of deliverables that developers must prepare for each SDK release:

- [ ] **WorkpathLib.aar** — build and test
- [ ] **Javadoc** — generate (`javadoc` → HTML + JAR)
- [ ] **21 Feature Overview PDFs** — update
- [ ] **SDK User Guide PDF** — update
- [ ] **Release Notes PDF** — write
- [ ] **23 Java samples** — build and package source/APK/HPK
- [ ] **23 Kotlin samples** — build and package source/APK/HPK
- [ ] **Extension samples** — update source
- [ ] **HPKTool** — package Win/Linux ZIPs
- [ ] **Simulator** — build installer
- [ ] **Open source notices** — update (THIRD-PARTY-LICENSES, Opensource Announcement)

---

## 5. Open Source Dependencies

Per `WorkpathSDK_Opensource_Announcement.txt`:

| Library | Version | License |
|---------|---------|---------|
| Google Gson | 2.8.1 | Apache License 2.0 |
| Simple XML Framework | 2.7.1-3 | Apache License 2.0 |
| Android Support Library | 26.1.0 | Apache License 2.0 |

> These are open source libraries used internally by the SDK library and must be disclosed with every release.

---

*→ Next: [WorkpathLib](../02_Library/WorkpathLib.md)*
