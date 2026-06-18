# E2/CDM API Endpoint Reference — Dune Platform

This document maps the **E2 (Extensibility) and CDM (Common Data Model) REST endpoints** exposed by the Dune firmware, as consumed by Workpath Services (DeviceServices/Standard layer). This is a platform-developer reference — not a solution-app-facing API guide.

---

## 1. E2 Public Endpoints (`/ext/`)

E2 endpoints are Workpath-specific firmware services. Each is identified by a **GUN (Globally Unique Name)** in the Discovery Tree. The DeviceServices layer in Workpath ServicesApp builds HTTPS calls using the `ServiceClientImpl` fluent API, which resolves endpoints from the Discovery Tree at runtime.

### Application Management Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/application/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/application/v1/applicationAccessPoints` | SolutionToken | List available application access points |
| **GET** | `/ext/application/v1/applicationAccessPoints/{pointId}` | SolutionToken | Get access point details |
| **GET** | `/ext/application/v1/applicationAgents` | SolutionToken | List registered application agents |
| **GET** | `/ext/application/v1/applicationAgents/{agentId}` | SolutionToken | Get application agent details |
| **GET** | `/ext/application/v1/applicationRuntime/currentContext` | **UIContextToken** | Get current application runtime context |
| **POST** | `/ext/application/v1/applicationRuntime/currentContext/resultIntent/initiateLaunch` | **UIContextToken** | Launch application (multipart) |
| **GET** | `/ext/application/v1/applicationRuntime/currentContext/runtimeChrome` | **UIContextToken** | Get runtime chrome info |
| **POST** | `/ext/application/v1/applicationRuntime/currentContext/runtimeChrome/refresh` | **UIContextToken** | Refresh the runtime chrome |
| **POST** | `/ext/application/v1/applicationRuntime/currentContext/runtimeChrome/beep` | **UIContextToken** | Trigger beep at control panel |
| **POST** | `/ext/application/v1/applicationRuntime/currentContext/runtimeChrome/resetInactivityTimer` | **UIContextToken** | Reset inactivity timer |
| **GET** | `/ext/application/v1/applicationRuntime/currentContext/startIntent` | **UIContextToken** | Get start intent |
| **POST** | `/ext/application/v1/applicationRuntime/currentContext/startIntent/exec` | **UIContextToken** | Execute start intent (multipart) |
| **POST** | `/ext/application/v1/applicationRuntime/currentContext/startIntent/exit` | **UIContextToken** | Exit application |
| **POST** | `/ext/application/v1/applicationRuntime/currentContext/startIntent/reset` | **UIContextToken** | Reset application state |
| **GET** | `/ext/application/v1/homescreen` | SolutionToken | Get homescreen configuration |
| **PUT** | `/ext/application/v1/homescreen` | SolutionToken | Modify homescreen configuration |
| **GET** | `/ext/application/v1/i18nAssets` | SolutionToken | List localization assets |
| **GET** | `/ext/application/v1/i18nAssets/{assetId}` | SolutionToken | Get localization asset |
| **GET** | `/ext/application/v1/messageCenterAgents` | SolutionToken | List message center agents |
| **GET** | `/ext/application/v1/messageCenterAgents/{agentId}/messages` | SolutionToken | List messages for agent |
| **POST** | `/ext/application/v1/messageCenterAgents/{agentId}/messages` | SolutionToken | Create message center message |
| **DELETE** | `/ext/application/v1/messageCenterAgents/{agentId}/messages/{msgId}` | SolutionToken | Delete message |

- **GUN**: `com.hp.ext.service.application.version.1`

**Agent Registration** — `ApplicationAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.application.version.1.type.applicationAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| _(application content fields)_ | ApplicationContent | — | Inherits from ApplicationContent; defines the walk-up application UI entry point, icons, localized labels, and launch configuration |

### Authentication Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/authentication/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/authentication/v1/authenticationAccessPoints` | SolutionToken | List available authentication mechanisms |
| **GET** | `/ext/authentication/v1/authenticationAccessPoints/{pointId}` | SolutionToken | Get authentication access point |
| **POST** | `/ext/authentication/v1/authenticationAccessPoints/{pointId}/initiateLogin` | SolutionToken | Initiate authentication process at the device UI |
| **GET** | `/ext/authentication/v1/authenticationAgents` | SolutionToken | List authentication agents (solution-provided) |
| **GET** | `/ext/authentication/v1/authenticationAgents/{agentId}` | SolutionToken | Get agent details |
| **POST** | `/ext/authentication/v1/authenticationAgents/{agentId}/login` | SolutionToken | Non-UI-initiated login via agent |
| **GET** | `/ext/authentication/v1/session` | SolutionToken | Get current login session info |
| **POST** | `/ext/authentication/v1/session/forceLogout` | SolutionToken | Force logout of current session |

- **GUN**: `com.hp.ext.service.authentication.version.1`

**Agent Registration** — `AuthenticationAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.authentication.version.1.type.authenticationAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |
| _(auth-specific fields)_ | Registration | — | Inherits from authentication-specific Registration type (pre/post prompt targets, etc.) |

### Copy Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/copy/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/copy/v1/copyAgents` | SolutionToken | List copy agents |
| **GET** | `/ext/copy/v1/copyAgents/{agentId}` | SolutionToken | Get agent details |
| **GET** | `/ext/copy/v1/copyAgents/{agentId}/copyJobs` | SolutionToken | List copy jobs for agent |
| **POST** | `/ext/copy/v1/copyAgents/{agentId}/copyJobs` | **UIContextToken** | Create copy job |
| **GET** | `/ext/copy/v1/copyAgents/{agentId}/copyJobs/{jobId}` | SolutionToken | Get copy job status |
| **POST** | `/ext/copy/v1/copyAgents/{agentId}/copyJobs/{jobId}/cancel` | SolutionToken | Cancel copy job |
| **GET** | `/ext/copy/v1/copyAgents/{agentId}/storedJobs` | SolutionToken | List stored (queued) jobs |
| **GET** | `/ext/copy/v1/copyAgents/{agentId}/storedJobs/{jobId}` | SolutionToken | Get stored job details |
| **POST** | `/ext/copy/v1/copyAgents/{agentId}/storedJobs/{jobId}/release` | SolutionToken | Release a stored job to print |
| **POST** | `/ext/copy/v1/copyAgents/{agentId}/storedJobs/{jobId}/remove` | SolutionToken | Remove a stored job |

- **GUN**: `com.hp.ext.service.copy.version.1`

**Agent Registration** — `CopyAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.copy.version.1.type.copyAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |
| `copyNotificationTarget` | CopyNotificationTarget | No | Workpath callback target to receive copy job notifications |

### Device Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/device/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/device/v1/identity` | SolutionToken | Get device identity (model, serial, etc.) |
| **GET** | `/ext/device/v1/status` | SolutionToken | Get device status |
| **GET** | `/ext/device/v1/deploymentInformation` | SolutionToken | Get deployment information |
| **GET** | `/ext/device/v1/scanner` | SolutionToken | Get scanner hardware info |
| **GET** | `/ext/device/v1/printEngine` | SolutionToken | Get print engine info |
| **GET** | `/ext/device/v1/email` | SolutionToken | Get device email configuration |
| **GET** | `/ext/device/v1/fax` | SolutionToken | Get device fax configuration |
| **GET** | `/ext/device/v1/alerts` | SolutionToken | List active device alerts |
| **GET** | `/ext/device/v1/alerts/{alertId}` | SolutionToken | Get specific alert details |
| **POST** | `/ext/device/v1/alerts/{alertId}/delete` | SolutionToken | Delete an alert |

- **GUN**: `com.hp.ext.service.device.version.1`

> No agent registration required for this service.

### Device Usage Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/deviceUsage/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/deviceUsage/v1/deviceUsageAgents` | SolutionToken | List device usage agents |
| **GET** | `/ext/deviceUsage/v1/deviceUsageAgents/{agentId}` | SolutionToken | Get agent details |
| **GET** | `/ext/deviceUsage/v1/deviceUsageAgents/{agentId}/lifetimeCounters` | SolutionToken | Get current lifetime device usage counters |

- **GUN**: `com.hp.ext.service.deviceUsage.version.1`

**Agent Registration** — `DeviceUsageAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.deviceUsage.version.1.type.deviceUsageAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |

### Job Statistics Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/jobStatistics/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/jobStatistics/v1/jobStatisticsAgents` | SolutionToken | List job statistics agents |
| **GET** | `/ext/jobStatistics/v1/jobStatisticsAgents/{agentId}` | SolutionToken | Get agent details |
| **GET** | `/ext/jobStatistics/v1/jobStatisticsAgents/{agentId}/jobs` | SolutionToken | List job statistics entries |
| **PUT** | `/ext/jobStatistics/v1/jobStatisticsAgents/{agentId}/jobs` | SolutionToken | Update last-processed sequence number |
| **GET** | `/ext/jobStatistics/v1/jobStatisticsAgents/{agentId}/jobs/{sequenceNumber}` | SolutionToken | Get statistics for a specific job |

- **GUN**: `com.hp.ext.service.jobStatistics.version.1`

**Agent Registration** — `JobStatisticsAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.jobStatistics.version.1.type.jobStatisticsAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |
| `notificationTarget` | RegistrationTarget | No | HTTPS callback URL for job statistics notifications |
| `criticalSolution` | bool | No | If true, device preserves job data until solution has acknowledged receipt |
| `defaultNotificationFilter` | ContentFilter | No | Default filter applied to notification payloads |
| `jobFilters` | List\<JobFilter\> | No | Filters to select which jobs are reported to this agent |

### Print Job Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/printJob/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/printJob/v1/printJobAgents` | SolutionToken | List registered print agents |
| **GET** | `/ext/printJob/v1/printJobAgents/{agentId}` | SolutionToken | Get agent details |

- **GUN**: `com.hp.ext.service.printJob.version.1`

**Agent Registration** — `PrintJobAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.printJob.version.1.type.printJobAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |

> Note: The printJob E2 service handles **agent registration only** — it does not manage job lifecycle. Actual print submission uses **IPP** protocol (not E2 REST). See [Hardware_Control.md](../03_Guides/Hardware_Control.md) Section 3 for the hybrid print architecture.

### Scan Job Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/scanJob/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/scanJob/v1/defaultOptions` | SolutionToken | Get default scan options per destination type |
| **GET** | `/ext/scanJob/v1/profile` | SolutionToken | Get scan ticket profile (valid options & constraints) |
| **GET** | `/ext/scanJob/v1/scanJobAgents` | SolutionToken | List available scan agents |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}` | SolutionToken | Get agent details |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}/scanJobs` | SolutionToken | List scan jobs for agent |
| **POST** | `/ext/scanJob/v1/scanJobAgents/{agentId}/scanJobs` | **UIContextToken** | Create scan job |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}/scanJobs/{jobId}` | SolutionToken | Get scan job status |
| **POST** | `/ext/scanJob/v1/scanJobAgents/{agentId}/scanJobs/{jobId}/cancel` | SolutionToken | Cancel scan job |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}/localScans` | SolutionToken | List completed local-folder scans |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}/localScans/{scanId}` | SolutionToken | Get local scan details |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}/localScans/{scanId}/files` | SolutionToken | List files in a local scan |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}/localScans/{scanId}/files/{fileId}` | SolutionToken | Get file metadata |
| **DELETE** | `/ext/scanJob/v1/scanJobAgents/{agentId}/localScans/{scanId}/files/{fileId}` | SolutionToken | Delete local scan file |
| **GET** | `/ext/scanJob/v1/scanJobAgents/{agentId}/localScans/{scanId}/files/{fileId}/data` | SolutionToken | Retrieve file content (multipart) |

- **GUN**: `com.hp.ext.service.scanJob.version.1`

**Agent Registration** — `ScanJobAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.scanJob.version.1.type.scanJobAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |
| `scanNotificationTarget` | ScanNotificationTarget | No | Workpath callback target to receive scan job notifications |

### Security Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/security/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/security/v1/securityAgents` | SolutionToken | List security agents |
| **GET** | `/ext/security/v1/securityAgents/{agentId}` | SolutionToken | Get agent details |
| **POST** | `/ext/security/v1/securityAgents/{agentId}/resolveSecurityExpression` | SolutionToken | Resolve a security macro expression |

- **GUN**: `com.hp.ext.service.security.version.1`

**Agent Registration** — `SecurityAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.security.version.1.type.securityAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |
| `corsEnabled` | bool | No | Allow JS from solution targets to access device-hosted APIs (CORS). Default: false |
| `declaredExpressionOperators` | List\<string\> | No | Custom security expression operators supported by this agent |

### Solution Manager Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/solutionManager/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/solutionManager/v1/installer` | SolutionToken | Get installer resource |
| **POST** | `/ext/solutionManager/v1/installer/installSolution` | SolutionToken | Install a solution (multipart request) |
| **POST** | `/ext/solutionManager/v1/installer/installModification` | SolutionToken | Install a solution modification (multipart request) |
| **POST** | `/ext/solutionManager/v1/installer/installRemote` | SolutionToken | Remote install a solution (multipart request) |
| **POST** | `/ext/solutionManager/v1/installer/uninstallSolution` | SolutionToken | Uninstall a solution |
| **POST** | `/ext/solutionManager/v1/installer/uninstallModification` | SolutionToken | Uninstall a solution modification |
| **GET** | `/ext/solutionManager/v1/installer/installerOperations` | SolutionToken | List all installer operations |
| **GET** | `/ext/solutionManager/v1/installer/installerOperations/{operationId}` | SolutionToken | Get installer operation details |
| **POST** | `/ext/solutionManager/v1/installer/installerOperations/{operationId}/requestCancel` | SolutionToken | Request cancellation of an installer operation |
| **GET** | `/ext/solutionManager/v1/solutions` | SolutionToken | List installed solutions |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}` | SolutionToken | Get solution details |
| **POST** | `/ext/solutionManager/v1/solutions/{solutionId}/reissueInstallCode` | SolutionToken | Reissue the solution install code |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/certificateAuthorities` | SolutionToken | List solution certificate authorities |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/certificateAuthorities/{caId}` | SolutionToken | Get certificate authority details |
| **DELETE** | `/ext/solutionManager/v1/solutions/{solutionId}/certificateAuthorities/{caId}` | SolutionToken | Delete certificate authority |
| **POST** | `/ext/solutionManager/v1/solutions/{solutionId}/certificateAuthorities/{caId}/export` | SolutionToken | Export a CA certificate (multipart response) |
| **POST** | `/ext/solutionManager/v1/solutions/{solutionId}/certificateAuthorities/import` | SolutionToken | Import a CA certificate (multipart request) |
| **POST** | `/ext/solutionManager/v1/solutions/{solutionId}/certificateAuthorities/export` | SolutionToken | Export all CA certificates (multipart response) |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/certificates` | SolutionToken | List solution certificates |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/certificates/{certId}` | SolutionToken | Get certificate details |
| **DELETE** | `/ext/solutionManager/v1/solutions/{solutionId}/certificates/{certId}` | SolutionToken | Delete certificate |
| **POST** | `/ext/solutionManager/v1/solutions/{solutionId}/certificates/import` | SolutionToken | Import a key/pair certificate (multipart request) |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/configuration` | SolutionToken | Get solution configuration metadata |
| **PUT** | `/ext/solutionManager/v1/solutions/{solutionId}/configuration` | SolutionToken | Modify solution configuration metadata |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/configuration/data` | SolutionToken | Get configuration data (multipart response) |
| **PUT** | `/ext/solutionManager/v1/solutions/{solutionId}/configuration/data` | SolutionToken | Replace configuration data (multipart request) |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/context` | SolutionToken | Get solution context |
| **PUT** | `/ext/solutionManager/v1/solutions/{solutionId}/context` | SolutionToken | Modify solution context |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/modifications` | SolutionToken | List solution modifications |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/modifications/{modId}` | SolutionToken | Get modification details |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/runtimeRegistrations` | SolutionToken | List runtime registrations for a solution |
| **POST** | `/ext/solutionManager/v1/solutions/{solutionId}/runtimeRegistrations` | SolutionToken | **Submit a service agent registration record** |
| **GET** | `/ext/solutionManager/v1/solutions/{solutionId}/runtimeRegistrations/{regId}` | SolutionToken | Get runtime registration details |
| **DELETE** | `/ext/solutionManager/v1/solutions/{solutionId}/runtimeRegistrations/{regId}` | SolutionToken | Delete a runtime registration |

- **GUN**: `com.hp.ext.service.solutionManager.version.1`

> **All service agent registrations use `POST /ext/solutionManager/v1/solutions/{solutionId}/runtimeRegistrations`**, with the record serialized as a `TypedObject` discriminated by GUN (e.g., `com.hp.ext.service.scanJob.version.1.type.scanJobAgentRegistrationRecord`).

### Supplies Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/supplies/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/supplies/v1/suppliesAgents` | SolutionToken | List supplies agents |
| **GET** | `/ext/supplies/v1/suppliesAgents/{agentId}` | SolutionToken | Get agent details |
| **GET** | `/ext/supplies/v1/suppliesAgents/{agentId}/suppliesConfiguration` | SolutionToken | Get supplies configuration |
| **GET** | `/ext/supplies/v1/suppliesAgents/{agentId}/suppliesInfo` | SolutionToken | Get device consumables information |
| **GET** | `/ext/supplies/v1/suppliesAgents/{agentId}/suppliesUsage` | SolutionToken | Get supplies usage statistics |

- **GUN**: `com.hp.ext.service.supplies.version.1`

**Agent Registration** — `SuppliesAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.supplies.version.1.type.suppliesAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |

### USB Accessories Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/usbAccessories/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/usbAccessories/v1/accessories` | SolutionToken | List connected USB accessories |
| **GET** | `/ext/usbAccessories/v1/accessories/{accessoryId}` | SolutionToken | Get accessory details |
| **GET** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic` | SolutionToken | Get generic USB mode info |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/open` | SolutionToken | Open accessory for generic USB operations |
| **GET** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}` | SolutionToken | Get open USB accessory state |
| **PUT** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}` | SolutionToken | Update open USB accessory attributes |
| **DELETE** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}` | SolutionToken | Close USB accessory |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}/reset` | SolutionToken | Reset the USB accessory |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}/controlRead` | SolutionToken | Read from endpoint 0 (structured control operation) |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}/controlWrite` | SolutionToken | Write to endpoint 0 (structured control operation) |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}/endpointHalt` | SolutionToken | Send endpoint halt command |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}/endpointRead` | SolutionToken | Read raw data from endpoint |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/generic/openAccessories/{openId}/endpointWrite` | SolutionToken | Write raw data to endpoint |
| **GET** | `/ext/usbAccessories/v1/accessories/{accessoryId}/hid` | SolutionToken | Get HID accessory info |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/hid/open` | SolutionToken | Open HID accessory |
| **GET** | `/ext/usbAccessories/v1/accessories/{accessoryId}/hid/openHIDAccessories/{openId}` | SolutionToken | Get open HID accessory state |
| **PUT** | `/ext/usbAccessories/v1/accessories/{accessoryId}/hid/openHIDAccessories/{openId}` | SolutionToken | Update open HID accessory attributes |
| **DELETE** | `/ext/usbAccessories/v1/accessories/{accessoryId}/hid/openHIDAccessories/{openId}` | SolutionToken | Close HID accessory |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/hid/openHIDAccessories/{openId}/readReport` | SolutionToken | Read report from HID control pipe |
| **POST** | `/ext/usbAccessories/v1/accessories/{accessoryId}/hid/openHIDAccessories/{openId}/writeReport` | SolutionToken | Write report to HID control pipe |
| **GET** | `/ext/usbAccessories/v1/usbAccessoriesAgents` | SolutionToken | List USB accessories agents |
| **GET** | `/ext/usbAccessories/v1/usbAccessoriesAgents/{agentId}` | SolutionToken | Get agent details |

- **GUN**: `com.hp.ext.service.usbAccessories.version.1`

**Agent Registration** — `UsbAccessoriesAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.usbAccessories.version.1.type.usbAccessoriesAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |
| `registrations` | List\<UsbRegistrationIdentification\> | Yes | USB device identifiers this agent will handle |
| `registrationTarget` | ServiceRegistrationTarget | No | HTTPS callback URL for accessory events |
| `enablePostRegistrationPromptCheck` | bool | No | Enable post-registration prompt checking |

### User Policy Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/userPolicy/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/userPolicy/v1/userPolicyAgents` | SolutionToken | List user policy agents |
| **GET** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}` | SolutionToken | Get agent details |
| **GET** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/configuration` | SolutionToken | Get policy agent configuration |
| **PUT** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/configuration` | SolutionToken | Modify policy agent configuration |
| **GET** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissionSets` | SolutionToken | List permission sets |
| **POST** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissionSets` | SolutionToken | Create a custom permission set |
| **POST** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissionSets/bulkUpdate` | SolutionToken | Bulk-update multiple permission sets |
| **GET** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissionSets/{setId}` | SolutionToken | Get permission set details |
| **PUT** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissionSets/{setId}` | SolutionToken | Modify permission set |
| **DELETE** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissionSets/{setId}` | SolutionToken | Delete custom permission set |
| **GET** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissions` | SolutionToken | List all permissions |
| **POST** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissions/bulkUpdate` | SolutionToken | Bulk-update multiple permissions |
| **GET** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissions/{permId}` | SolutionToken | Get permission details |
| **PUT** | `/ext/userPolicy/v1/userPolicyAgents/{agentId}/permissions/{permId}` | SolutionToken | Modify permission |

- **GUN**: `com.hp.ext.service.userPolicy.version.1`

**Agent Registration** — `UserPolicyAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.userPolicy.version.1.type.userPolicyAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |

### WebHook Service
| Method | Path | Token | Description |
|---|---|---|---|
| **GET** | `/ext/webHook/v1/capabilities` | SolutionToken | Get service capabilities |
| **GET** | `/ext/webHook/v1/webHookAgents` | SolutionToken | List web hook agents |
| **GET** | `/ext/webHook/v1/webHookAgents/{agentId}` | SolutionToken | Get agent details |

- **GUN**: `com.hp.ext.service.webHook.version.1`

**Agent Registration** — `WebHookAgentRegistrationRecord`  _(GUN: `com.hp.ext.service.webHook.version.1.type.webHookAgentRegistrationRecord`)_

| Field | Type | Required | Description |
|---|---|---|---|
| `agentId` | Guid | Yes | Agent UUID (assigned by solution) |
| `name` | string | Yes | Unlocalized name (debug use only) |
| `localizedName` | LocalizedStringReference | No | Localized display name |
| `localizedDescription` | LocalizedStringReference | No | Localized description |
| `webHooks` | List\<WebHook\> | Yes | List of 1–256 HTTPS webhook endpoint definitions to register |

---

## 2. CDM Public Endpoints (`/cdm/`)

CDM endpoints are standard device management APIs following OXPd conventions. They are **not Workpath-specific** — they exist on all E2-capable HP devices.

### Services Discovery
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/servicesDiscovery/` | Discovery tree root — lists all available services and their GUNs |

- **GUN**: `com.hp.cdm.servicesDiscovery`
- **Client**: `DiscoveryServiceClientImpl`
- **Critical**: Called during `StandardDeviceManagementService.initialize()` to build the discovery tree cache.

### System / Device Identity
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/system/v1/configuration` | System configuration |
| **GET** | `/cdm/device/identity` | Device identity (model, serial, etc.) |
| **GET** | `/cdm/device/time` | Current device time |
| **PUT** | `/cdm/device/time` | Set device time (used during boot sync) |

### Alerts
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/alert/v1/` | List active alerts |
| **GET** | `/cdm/alert/v1/{alertId}` | Get specific alert details |

### PubSub (Event Subscription)
| Method | Path | Description |
|---|---|---|
| **POST** | `/cdm/pubsub/v2/subscriptions` | Create event subscription |
| **DELETE** | `/cdm/pubsub/v2/subscriptions/{subId}` | Delete subscription |
| **GET** | `/cdm/pubsub/v2/subscriptions` | List active subscriptions |

### Network / Print Services
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/network/v1/printServices` | Network print service configuration |

### Storage Devices
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/storageDevices/v1/removableDevices` | List removable storage devices (USB) |

### USB Host
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/usbHost/v1/` | USB host configuration |

### Job Ticket
| Method | Path | Description |
|---|---|---|
| **POST** | `/cdm/jobTicket/v1/` | Submit job ticket (email, fax) |

### Security
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/security/v1/authenticationAgents` | List authentication agent types |
| **POST** | `/cdm/security/v1/authenticationAgents` | Register authentication agent |

### OAuth2
| Method | Path | Description |
|---|---|---|
| **POST** | `/cdm/oauth2/v1/token` | Request OAuth2 token |
| **POST** | `/cdm/oauth2/v1/authorize` | Authorization flow |

### Job Management
| Method | Path | Description |
|---|---|---|
| **GET** | `/cdm/jobManagement/v1/queue` | Active job queue |
| **GET** | `/cdm/jobManagement/v1/jobs/{jobId}` | Get job detail |
| **GET** | `/cdm/jobManagement/v1/jobs/{jobId}/pages` | Get page details |

---

## 3. E2 Workpath Interop Endpoints (`/cdm/e2WorkpathInterop/v1/`)

These endpoints manage the Workpath platform's relationship with the Dune firmware. They are used by **Workpath System** and **Workpath Services** platform components, not by individual solution apps.

| Method | Path | Used By | Description |
|---|---|---|---|
| **POST** | `/cdm/e2WorkpathInterop/v1/workpathGateway` | Workpath System | Register as Workpath gateway |
| **GET** | `/cdm/e2WorkpathInterop/v1/workpathGateway` | Workpath System | Get gateway status |
| **GET** | `/cdm/e2WorkpathInterop/v1/appToken/{solutionId}` | AppTokenManager | Obtain per-solution E2 token (1hr TTL) |
| **POST** | `/cdm/e2WorkpathInterop/v1/websocketConnection` | Workpath System | Establish WebSocket for async events |
| **GET** | `/cdm/e2WorkpathInterop/v1/networkConfiguration` | Workpath System | Get network config for Android container |
| **GET** | `/cdm/e2WorkpathInterop/v1/capabilities` | Workpath Services | Query interop capabilities |
| **POST** | `/cdm/e2WorkpathInterop/v1/clientInfo` | Workpath Services | Report client platform info |
| **GET** | `/cdm/e2WorkpathInterop/v1/cdmPubData` | Workpath Services | Get CDM pub/sub data |

### Token Endpoints (via AppTokenManager)

```
GET /cdm/e2WorkpathInterop/v1/appToken/{solutionId}
→ Returns: { "token": "...", "expiresIn": 3600 }
```

`AppTokenManager` caches these per-solution tokens with **1-hour TTL** (pre-emptively refreshed at 50 minutes). When a token's age exceeds the threshold, the next `getSolutionToken(solutionId)` call automatically fetches a new one.

### WebSocket Endpoint

```
POST /cdm/e2WorkpathInterop/v1/websocketConnection
→ Returns: { "websocketPath": "/e2ws/..." }
```

Workpath System constructs the full URL as `wss://{device_ip}{websocketPath}` and establishes a persistent WebSocket connection. Messages arrive with a `target` field:

| WebSocket Target | Description | Handler |
|---|---|---|
| `systemManagement` | System-level events (reboot, update) | `WSCallbackSystemManagement` |
| `appManagement` | App install/uninstall/lifecycle events | Package Manager `DuneCallbackService` |
| `channelMessage` | E2 channel messages (job progress, callbacks) | `AppChannelCallbackRegistry` |
| `cdmPubMessage` | CDM pub/sub notifications | `WSCallbackCdmPubMsg` / `CdmPubSubHandler` |
| `gatewayMessage` | Gateway control messages | `WSCallbackGateway` |
| `statusCheck` | Heartbeat / connection status | `WSCallbackStatusCheck` |
| `logManagement` | Remote log control | `LogDaemon` |

---

## 4. IPP Protocol (Print Only)

Print job submission uses IPP (Internet Printing Protocol), not E2 REST:

| Method | Endpoint | Discovery |
|---|---|---|
| **POST** | `/ipp/print` | GUN `com.hp.standard.feature.pwgIpp` → `homeUrl` link |

The IPP endpoint is resolved from the Discovery Tree using the `com.hp.standard.feature.pwgIpp` GUN. `StandardDevicePrintJobService` calls the discovered URL directly with IPP-formatted payloads.

---

## 5. Discovery Tree Structure

The Discovery Tree is fetched on initialization by `DiscoveryServiceClientImpl.discover()`:

```
GET /cdm/servicesDiscovery/
→ ServicesDiscoveryTree
   ├── services[]
   │   ├── { gun: "com.hp.ext.service.scanJob.version.1", links: [...] }
   │   ├── { gun: "com.hp.ext.service.printJob.version.1", links: [...] }
   │   ├── { gun: "com.hp.ext.service.copy.version.1", links: [...] }
   │   ├── { gun: "com.hp.standard.feature.pwgIpp", links: [...] }
   │   └── ...
   └── links[]
       ├── { rel: "self", href: "/cdm/servicesDiscovery/" }
       └── ...
```

Each service entry contains:
- **gun**: Globally Unique Name
- **links[]**: Available endpoints for that service (with `rel` indicating the relationship type)

The `ServiceClientImpl` base class resolves base URLs from the tree at request time:
```java
// Internal pattern — ServiceClientImpl resolves endpoint from tree
ScanJobServiceClientImpl client = new ScanJobServiceClientImpl(
    httpClient, deviceIPAddress, discoveryTree);
// client internally finds the /ext/scanJob/v1/ base URL from the tree
```

---

## 6. IDeviceScanJobService Interface (Full Signatures)

Representative example of a DeviceServices interface as consumed by Let modules:

```java
public interface IDeviceScanJobService {
    boolean isSupported();
    Capabilities getCapabilities(String packageName);
    DefaultOptions getDefaultOptions(String packageName);
    Profile getProfile(String packageName);

    // Scan job lifecycle
    ScanJob createScanJob(String packageName, ScanJob_Create job);
    ScanJob getScanJob(String packageName, String jobId);
    ScanJob_Cancel cancelScanJob(String packageName, String jobId);

    // Local scan access (scan-to-local-folder results)
    LocalScans getLocalScans(String packageName);
    LocalScan getLocalScan(String packageName, String scanId);
    Files getLocalScanFiles(String packageName, String scanId);
    File getLocalScanFile(String packageName, String scanId, String fileId);
    byte[] getLocalScanFileData(String packageName, String scanId, String fileId);
    void deleteLocalScanFile(String packageName, String scanId, String fileId);

    void registerNotificationCallback(IE2PayloadCallback<ScanNotification> callback);
}
```

> See [DeviceServices.md](../02_Components/DeviceServices.md) for the full interface list and [Hardware_Control.md](../03_Guides/Hardware_Control.md) for implementation patterns.
