# API Surface

> **Audience**: Workpath SDK developers
> **Version**: HP Workpath SDK v1.6.3

---

## 1. Package Overview

WorkpathLib.aar provides **34 packages** with **150+ classes**, **100+ enums**, **6 broadcast actions**, and **9 permissions**.

```mermaid
graph LR
    subgraph "com.hp.workpath.api"
        CORE[Core<br/>Workpath, Result]
        SCAN[scanner]
        PRINT[printer]
        COPY[copier]
        ACCESS[access]
        AUTH[authorization]
        DEVICE[device<br/>events, settings]
        USAGE[deviceusage<br/>printer, scanner]
        JOB[job]
        CONFIG[config]
        LAUNCH[launcher]
        ATT[attestation]
        MASS[massstorage]
        EMAIL[helper.email]
        STATS[statistics<br/>jobinfo/*]
        SUPPLY[supplies<br/>supplyinfo]
        WEB[webservices]
        ACC[accessory<br/>hid]
    end
```

---

## 2. Complete Package Catalog

### 2.1 Core Package

**Package**: `com.hp.workpath.api`

| Type | Name | Description |
|------|------|-------------|
| Interface | `ILetObserver` | Generic interface for monitoring job event changes |
| Class | `Workpath` | SDK entry point — singleton, `initialize()` must be called |
| Class | `Workpath.actions` | Broadcast action constant definitions |
| Class | `Workpath.permissions` | Permission constant definitions |
| Class | `Result` | API call result (`RESULT_OK` / `RESULT_FAIL`) |
| Enum | `Result.ErrorCode` | Error code enumeration |
| Exception | `SsdkUnsupportedException` | SDK not installed / version mismatch |
| Exception | `CapabilitiesExceededException` | Request exceeds device capabilities |

**Result.ErrorCode values:**

| Code | Description | Since |
|------|-------------|-------|
| `INVALID_PARAM` | Invalid parameter | API 1 |
| `CONNECTION_ERROR` | Connection error | API 1 |
| `SERVICE_ERROR` | Service error | API 1 |
| `JOB_FAILURE` | Job failure | API 1 |
| `AUTHENTICATION_ERROR` | Authentication error | API 1 |
| `UNAUTHORIZED` | Not authorized | API 1 |
| `UNKNOWN` | Unknown error | API 1 |
| `NOT_SUPPORTED` | Feature not supported | API 1 |
| `SYSTEM_ERROR` | System error | API 1 |
| `UNAVAILABLE` | Service unavailable | API 9 |

---

### 2.2 Scanner (`com.hp.workpath.api.scanner`)

**Classes (26)**:

| Category | Classes |
|----------|---------|
| Service | `ScannerService`, `ScannerStatus` |
| Attributes | `ScanAttributes` + Builders (`EmailBuilder`, `FtpBuilder`, `HttpBuilder`, `MeBuilder`, `NetworkFolderBuilder`, `ScanAttributesBuilder<T>`, `ScanToUriBuilder<T>`, `UsbBuilder`) |
| Capabilities | `ScanAttributesCaps` |
| Reader | `ScanAttributesReader` |
| Task | `ScanletAttributes` + `Builder` |
| Email | `EmailAddressInfo`, `EmailAttributes` + `Builder` |
| File Options | `FileOptionsAttributes` + `Builder`, `FileOptionsAttributesCaps`, `FileOptionsAttributesReader` |
| Network | `NetworkCredentialsAttributes` + `Builder`, `SmtpAttributes` + `Builder` |
| Helper | `Margins`, `Range`, `StatusInfo` |

**Enums (31)**: `AutomaticStraightenMode`, `AutomaticToneMode`, `BackgroundCleanup`, `BlankImageRemovalMode`, `CaptureMode`, `ColorDropoutMode`, `ColorMode`, `ContrastAdjustment`, `CropMode`, `DarknessAdjustment`, `Destination`, `DocumentFormat`, `Duplex`, `EraseMarginUnit`, `JobAssemblyMode`, `MediaSource`, `MediaWeightAdjustment`, `MisfeedDetectionMode`, `Orientation`, `OutputQuality`, `PdfCompressionMode`, `ProgressDialogMode`, `Resolution`, `ScanPreview`, `ScanSize`, `SharpnessAdjustment`, `SplitAttachmentByPage`, `TextPhotoOptimization`, `TiffCompressionMode`, `TransmissionMode`, `XpsCompressionMode`

**+ Sub-enums**: `SmtpAttributes.TransportMode`, `StatusInfo.StatusCondition`, `FileOptionsAttributes.OcrLanguage`

---

### 2.3 Printer (`com.hp.workpath.api.printer`)

**Classes (16)**:

| Category | Classes |
|----------|---------|
| Service | `PrinterService`, `PrinterStatus` |
| Attributes | `PrintAttributes` + Builders (`PrintCommonAttributesBuilder<T>`, `PrintFromHttpBuilder`, `PrintFromStorageBuilder`, `PrintFromStreamBuilder`, `PrintFromUsbBuilder`) |
| Capabilities | `PrintAttributesCaps` |
| Reader | `PrintAttributesReader` |
| Task | `PrintletAttributes` + `Builder` |
| Network | `NetworkCredentialsAttributes` + `Builder` |
| Helper | `StatusInfo`, `TrayInfo` |

**Enums (17)**: `AutoFit`, `CollateMode`, `ColorMode`, `DocumentFormat`, `Duplex`, `Finishings`, `Orientation`, `OutputBin`, `PaperSize`, `PaperSource`, `PaperType`, `PrintQuality`, `Source`, `StapleMode`, `StatusInfo.Status`, `StatusInfo.StatusReason`, `TrayInfo.Status`

---

### 2.4 Copier (`com.hp.workpath.api.copier`)

**Classes (16)**:

| Category | Classes |
|----------|---------|
| Service | `CopierService` |
| Attributes | `CopyAttributes` + Builders (`CopyAttributesBuilder<T>`, `CopyBuilder`, `StoreCopyBuilder`) |
| Capabilities | `CopyAttributesCaps` |
| Reader | `CopyAttributesReader` |
| Task | `CopyletAttributes` + `Builder` |
| Stored Job | `StoredJobAttributes` + `StoredJobBuilder`, `StoredJobInfo` |
| Credentials | `JobCredentialsAttributes` + `Builder` |
| Helper | `FloatRange`, `Range` |

**Enums (39)**: `BackgroundCleanup`, `BookletBordersEachPage`, `BookletFinishingOption`, `BookletFormat`, `CaptureMode`, `CollateMode`, `ColorMode`, `ContrastAdjustment`, `CopyPreview`, `DarknessAdjustment`, `Duplex`, `EraseMarginUnit`, `FoldMode`, `ImageShiftReduceToFit`, `ImageShiftUnits`, `JobAssemblyMode`, `JobExecutionMode`, `NumberUpDirection`, `NumberUpMode`, `Orientation`, `OutputBin`, `PaperSize`, `PaperSource`, `PaperType`, `ProgressDialogMode`, `PunchMode`, `RetentionMode`, `ScaleMode`, `ScanSize`, `ScanSource`, `SharpnessAdjustment`, `StampPosition`, `StapleOption`, `TextGraphicsOptimization`, `WatermarkBackgroundPattern`, `WatermarkMessageType`, `WatermarkOnlyFirstPage`, `WatermarkRotate45`, `WatermarkType`, `JobCredentialsAttributes.PasswordType`

---

### 2.5 Access / Authentication (`com.hp.workpath.api.access`)

**Classes (18)**:

| Category | Classes |
|----------|---------|
| Service | `AccessService`, `AbstractAuthenticationService` |
| Attributes | `AuthenticationAttributes` + Builders (`AuthenticationAttributesBuilder<T>`, `LdapBuilder`, `NovellBuilder`, `OtherBuilder`, `PinBuilder`, `WindowsBuilder`, `WindowsSmartCardBuilder`) |
| Token | `DeviceToken`, `SignInAction` |
| User | `EmailAddressInfo`, `Principal` |
| Overrides | `UserOverridesAttributes` + `Builder`, `UserPreferencesAttributes` + `Builder` |

**Enums**: `AuthenticationAttributes.AuthenticationType`, `Principal.SimpleAuthority`, `SignInAction.Action`

---

### 2.6 Authorization (`com.hp.workpath.api.authorization`)

**Classes (18)**: `AbstractAuthorizationService`, `AuthorizationService`, `AuthenticatedUserInfo`, `ChangeNotificationEventData`, `EmailAddressInfo`, `KeyValuePair`, `LocalizedString`, `Permission`, `PermissionToSignInMethod`, `ProxyConfiguration` + `Builder`, `SignInMethod`, `Timestamp`, `UserAuthorizationData`, `UserAuthorizationResult` + `Builder`, `UserOverrides` + `Builder`

**Enums**: `AuthenticationType`, `ChangeNotificationEventCode`

---

### 2.7 Device (`com.hp.workpath.api.device`)

| Sub-package | Classes |
|-------------|---------|
| `device` | `DeviceService`, `DeviceAttributeBase` (interface), `DeviceAttribute` (enum) |
| `device.events` | `DeviceEventsService`, `DeviceEventsService.AbstractDeviceEventsChangeObserver`, `DeviceEvent`, `Timestamp` |
| `device.settings` | `DeviceSettingsService` |

---

### 2.8 Device Usage (`com.hp.workpath.api.deviceusage`)

| Sub-package | Classes |
|-------------|---------|
| `deviceusage` | `DeviceUsageService`, `DeviceUsageInfo`, `Plex`, `SubUnitInfo` |
| `deviceusage.printer` | `PrinterInfo` |
| `deviceusage.scanner` | `ScannerInfo` |

**Enums**: `SubUnitInfo.ColorMode`, `SubUnitInfo.JobCategory`, `SubUnitInfo.MediaSize`

---

### 2.9 Job Monitoring (`com.hp.workpath.api.job`)

| Type | Name | Description |
|------|------|-------------|
| Interface | `JobInfo.JobData` | Job data interface |
| Interface | `JobInfo.JobState` | Job state interface |
| Class | `JobService` | Job management service |
| Class | `JobService.AbstractJobletObserver` | Job event observer |
| Class | `JobInfo` | Job information |
| Class | `JobletAttributes` + `Builder` | Job behavior settings |
| Class | `CopyJobData`, `PrintJobData`, `ScanJobData` | Per-type job data |
| Class | `CopyJobState`, `PrintJobState`, `ScanJobState` | Per-type job state |

**Enums**: `JobInfo.JobType`, `CopyJobState.ActivityState`, `CopyJobState.State`, `PrintJobState.State`, `ScanJobState.ActivityState`, `ScanJobState.State`

---

### 2.10 Config (`com.hp.workpath.api.config`)

| Type | Name |
|------|------|
| Class | `ConfigService` |
| Class | `ConfigService.AbstractConfigChangeObserver` |

---

### 2.11 Launcher (`com.hp.workpath.api.launcher`)

| Type | Name |
|------|------|
| Class | `LauncherService` |
| Enum | `LaunchAction` |

---

### 2.12 Attestation (`com.hp.workpath.api.attestation`)

| Type | Name |
|------|------|
| Class | `AppToken` |
| Class | `AttestationService` |

---

### 2.13 Mass Storage (`com.hp.workpath.api.massstorage`)

| Type | Name |
|------|------|
| Class | `MassStorageService` |
| Class | `MassStorageService.AbstractMassStorageChangeObserver` |
| Class | `MassStorageInfo`, `CustomerDataFile`, `CustomerDataFileUtils` |
| Enum | `MassStorageInfo.Protocol`, `MassStorageInfo.StorageType` |

---

### 2.14 Email Helper (`com.hp.workpath.api.helper.email`)

**Classes**: `Email`, `EmailAddressInfo`, `EmailAttributes` + `Builder`, `NetworkCredentialsAttributes` + `Builder`, `ProxyAttributes` + `Builder`, `SmtpAttributes` + `Builder`

**Enums**: `ProxyAttributes.ProxyConfigurationMode`, `SmtpAttributes.TransportMode`

---

### 2.15 Statistics (`com.hp.workpath.api.statistics`)

**Main**: `StatisticsService`, `StatisticsService.AbstractStatisticsNotificationObserver`, `StatisticsJobData`

**Sub-packages** (9 under jobinfo):

| Sub-package | Classes |
|-------------|---------|
| `statistics.jobinfo` | Base job info types |
| `statistics.jobinfo.driverinfo` | Driver info |
| `statistics.jobinfo.emailinfo` | Email info |
| `statistics.jobinfo.faxinfo` | Fax info |
| `statistics.jobinfo.folderinfo` | Folder info |
| `statistics.jobinfo.ftpinfo` | FTP info |
| `statistics.jobinfo.httpinfo` | HTTP info |
| `statistics.jobinfo.print` | Print job info |
| `statistics.jobinfo.scan` | Scan job info |
| `statistics.jobinfo.userinfo` | User info |

---

### 2.16 Supplies (`com.hp.workpath.api.supplies`)

| Type | Name | Note |
|------|------|------|
| Class | `SuppliesService` | |
| Class | `SupplyList`, `CounterGroup` | |
| Sub-package | `supplies.supplyinfo` | `Supply` + deprecated classes (pre-API 8) |

> `CartridgeInfo`, `ApproximatePagesRemaining`, `Counter`, `CycleCount`, `CycleLimit`, `FixedPointNumber`, `Manufacturer` are deprecated since API 8.

---

### 2.17 Web Services (`com.hp.workpath.api.webservices`)

| Type | Name |
|------|------|
| Interface | `AbstractWebServices.Callback`, `Callback`, `Callback.HttpCallback` |
| Class | `AbstractWebServices`, `HttpRequest`, `HttpResponse`, `WebServices`, `WebServicesAttributes` + `Builder` |
| Enum | `HttpRequest.HeaderKey` |

---

### 2.18 Accessory (`com.hp.workpath.api.accessory`)

| Sub-package | Type | Name |
|-------------|------|------|
| `accessory` | Class | `AbstractAccessoryService`, `AccessoryInfo`, `ReportEventInfo` |
| `accessory` | Enum | `AccessoryInfo.AccessoryClass`, `RegistrationType` |
| `accessory.hid` | Class | `AccessoryService`, `AbstractAccessoryObserver`, `AbstractAccessoryStartObserver`, `HIDAccessoryInfo`, `HIDInfo`, `HIDReport`, `HIDReportEventInfo` |
| `accessory.hid` | Enum | `EventCode`, `HIDReportType` |

---

## 3. Broadcast Actions (Since API 9)

| Constant | Description | Required Permission |
|----------|-------------|---------------------|
| `SIGN_IN` | Broadcast when sign-in occurs | `RECEIVE_SIGN_IN_OUT_EVENT` |
| `SIGN_OUT` | Broadcast when sign-out occurs | `RECEIVE_SIGN_IN_OUT_EVENT` |
| `JOB_COMPLETED` | Broadcast when a walk-up job completes | `RECEIVE_JOB_COMPLETED_EVENT` |
| `CONFIG_CHANGED` | Broadcast when app config changes | `RECEIVE_CONFIG_CHANGED_EVENT` |
| `WAKE_UP` | Broadcast when device wakes from sleep | `RECEIVE_SLEEP_WAKEUP_EVENT` |
| `SLEEP` | Broadcast when device enters sleep | `RECEIVE_SLEEP_WAKEUP_EVENT` |

---

## 4. Permissions

| Constant | Description | Since |
|----------|-------------|-------|
| `SDK_ACCESS_DEVICE_EVENTS_PERMISSION` | Access DeviceEventsService | API 5 |
| `SDK_ACCESS_DEVICE_USAGE_PERMISSION` | Access DeviceUsageService | API 5 |
| `SDK_ACCESS_STATISTICS_PERMISSION` | Access StatisticsService | API 5 |
| `SDK_ACCESS_SUPPLIES_PERMISSION` | Access SuppliesService | API 5 |
| `DISABLE_PRINTING_PORTS_PERMISSION` | Disable external printing ports | API 9 |
| `RECEIVE_SIGN_IN_OUT_EVENT` | Receive sign-in/out broadcasts | API 9 |
| `RECEIVE_CONFIG_CHANGED_EVENT` | Receive config-changed broadcasts | API 9 |
| `RECEIVE_JOB_COMPLETED_EVENT` | Receive job-completed broadcasts | API 9 |
| `RECEIVE_SLEEP_WAKEUP_EVENT` | Receive sleep/wakeup broadcasts | API 9 |

---

## 5. API Totals Summary

| Metric | Count |
|--------|-------|
| Packages | 34 |
| Classes | 150+ |
| Enums | 100+ |
| Interfaces | ~10 |
| Exceptions | 2 |
| Broadcast Actions | 6 |
| Permissions | 9 |
| Error Codes | 10 |
| Key Service Classes | 19 |

---

*→ Next: [API Patterns](API_Patterns.md)*
