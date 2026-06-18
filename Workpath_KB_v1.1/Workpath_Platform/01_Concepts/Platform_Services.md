# Workpath Platform Services

## Overview

**Workpath Platform Services** are a set of pre-installed privileged apps that provide the Workpath platform's core services within the Android runtime environment, including system services (platform startup and system management), package management services (app installation and removal), and Workpath SDK API services (implementing and exposing Workpath SDK APIs to third-party apps over Android IPC, and translating those SDK calls into firmware API requests to the Dune platform).


> **Relationship to Architecture.md**: Workpath Platform Services correspond to the **Workpath Platform Services** sub-layer inside the Workpath Platform described in [Architecture.md Section 1.2](./Architecture.md#12-component-architecture).

| App | APK | Primary Responsibility |
|---|---|---|
| **SystemApp** | `System-dune.apk` | Platform boot, App launch, screen switching, license enforcement |
| **PackageManagerApp** | `WorkpathPackageManager-dune.apk` | APK installation, APK verification, App lifecycle |
| **ServicesApp** | `WorkpathServices-dune.apk` | Workpath SDK API services implementation |
| **LogDaemon** | `LogDaemon-dune.apk` | Workpath Log management for both platform and apps |

---

## 1. Pre-installed Apps for Workpath Platform Services

```plantuml
@startuml ServicePlatform_Overview
skinparam defaultTextAlignment center
skinparam defaultFontSize 12
skinparam rectangleBorderColor #757575
skinparam arrowColor #555555
skinparam ArrowFontSize 11
top to bottom direction

rectangle "**Workpath App**\nWorkpathLib.aar (SDK Library)" as APP #E8F5E9

rectangle "**Workpath Platform Services**" as WSP #C8E6C9 {
    rectangle "**SystemApp**\n(System-dune.apk)" as SYS #C8E6C9
    rectangle "**PackageManagerApp**\n(WorkpathPackageManager-dune.apk)" as PKG #C8E6C9
    rectangle "**ServicesApp**\n(WorkpathServices-dune.apk)" as SVC #C8E6C9
    rectangle "**LogDaemon**\n(LogDaemon-dune.apk)" as LD #C8E6C9
    SYS -right[hidden]- PKG
    PKG -right[hidden]- SVC
}

rectangle "**Dune Platform**\nE2 APIs / CDM APIs / E2 Workpath Interop" as DUNE #EEEEEE

APP  -down-->  SVC  : Android IPC
SYS  <-->  DUNE : WebSocket
SYS  -down->  DUNE : HTTPS REST
PKG  -down->  DUNE : HTTPS REST
SVC  -down->  DUNE : HTTPS REST
LD   <-->  DUNE : Bind Mount
@enduml
```

---

### 1.1 SystemApp

**Package**: `com.hp.jetadvantage.link.system` | **APK**: `System-dune.apk`

SystemApp is a system-privileged app. It is the first Workpath component to start, triggered by the `BOOT_COMPLETED` broadcast from the Android OS.

| Responsibility | Description |
|---|---|
| **Boot orchestration & platform readiness** | Receives `BOOT_COMPLETED`, initializes the startup sequence, and updates the platform readiness signal to Dune |
| **E2 WebSocket bridge** | Maintains a persistent WebSocket connection to Dune firmware for asynchronous event callbacks |
| **Screen switching** | Manages transitions between the Dune control panel UI and the Android (Workpath) UI |
| **License enforcement** | Validates app licenses; enables or disables installed apps accordingly |
| **Locale & time sync** | Synchronizes Android locale and system clock with printer firmware |


### 1.2 ServicesApp

**Package**: `com.hp.jetadvantage.link.services` | **APK**: `WorkpathServices-dune.apk`

ServicesApp is the API backend of the Workpath Platform. It implements Workpath APIs, communicating with the App (via `WorkpathLib.aar`) over Android IPC (`ContentProvider`, `BroadcastReceiver`, Bound Service with AIDL Binder), and translates each SDK call into one or more HTTPS REST calls to the Dune Platform's E2 or CDM APIs.

| Responsibility | Description |
|---|---|
| **Workpath SDK API implementation** | Implements Workpath SDK APIs, receiving requests from `WorkpathLib.aar` via Android IPC (`ContentProvider`, `BroadcastReceiver`, Bound Service with AIDL Binder) |
| **Hosting Workpath services** | Hosts ServiceLet modules (`ScanLet`, `PrintLet`, `CopyLet`, `DeviceLet`, etc.), each implementing a specific device capability category |
| **Firmware API proxy** | Translates each SDK call into HTTPS REST requests to the Dune E2 or CDM APIs for accessing resources of the device, authenticated via Bearer access token |

### Internal Layered Architecture
The internal structure of ServicesApp follows a layered architecture. The Service Module Layer, composed of individual ServiceLet modules, receives and processes service-specific requests from the App. Beneath it, the DeviceServices Layer connects to the device and abstracts the device’s resources, and manages solution access tokens for each app to access its dedicated E2 service agents and resources.

```plantuml
@startuml ServicesApp_Layers
skinparam defaultTextAlignment center
skinparam defaultFontSize 12
skinparam rectangleBorderColor #757575
skinparam arrowColor #333333
skinparam ArrowFontSize 11
top to bottom direction

rectangle "**Workpath App**\nWorkpathLib.aar (SDK Library)" as APP #E8F5E9

rectangle "**ServicesApp** (WorkpathServices-dune.apk)" as SVCAPP #C8E6C9 {
    rectangle "**ServiceModules Layer**\n | AccessLet | AccessoryLet | ConfigLet | CopyLet | DeviceEventLet | DeviceSettingLet | JobLet | PrintLet | ScanLet | StatisticsLet | ..." as SVC #C8E6C9

    rectangle "**DeviceServices Layer**" as DS #C8E6C9 {
        rectangle "DS Interfaces sub-layer\n(Java interface definitions)" as DSI #DCEDC8
        rectangle "DS Implementation sub-layer\n(Standard / Sim)" as DSIM #DCEDC8
        rectangle "DeviceConnect sub-layer\n(HTTPS client, connection mgmt)" as DSC #DCEDC8
        DSI -down-> DSIM
        DSIM -down-> DSC
    }

    SVC -down-> DSI
}

rectangle "**Dune Platform**\nE2 APIs | CDM APIs | E2 Workpath Interop" as DUNE #EEEEEE

APP  -down-->  SVC  : Android IPC
DSC  -down->  DUNE : HTTPS REST / Bearer token

@enduml
```

### 1.3 PackageManagerApp

**Package**: `com.hp.jetadvantage.link.packagemanager` | **APK**: `WorkpathPackageManager-dune.apk`

PackageManagerApp handles the installation, verification, and removal of Workpath Apps on the device. In previous platforms (Jedi/Jolt), PackageManagerApp provided the endpoint for app installation. However, in Dune, the Dune E2 Solution Manager service provides the endpoints for both E2 solution and Workpath app installation and removal, orchestrates the entire app installation process, and performs signing and bundle verification for app bundles (.hpk2). As a result, the role of Workpath PackageManagerApp in Dune is reduced to collaborating with the E2 Solution Manager and handling only the APK installation step within the overall process.

| Responsibility | Description |
|---|---|
| **APK installation** | Receives an install notification from E2 Solution Manager and installs the APK via Android `PackageManager` |
| **Signature verification** | Verifies the cryptographic signature of each APK  |
| **Metadata persistence** | Stores installed app metadata in its internal database |
| **Solution lifecycle** | Coordinates update and removal operations with E2 Solution Manager |

### 1.4 LogDaemonApp

**Package**: `com.hp.jetadvantage.link.logdaemon` | **APK**: `LogDaemon-dune.apk`

LogDaemon is a background service responsible for collecting and forwarding log output from the Workpath platform and all Workpath Apps to the Dune firmware side when requested.

| Responsibility | Description |
|---|---|
| **Log collection** | Collects log output from Workpath platform components and installed Workpath Apps |
| **Log forwarding** | Writes collected logs to a shared directory exposed to the Dune platform via bind mount |
| **Log lifecycle management** | Manages log rotation and retention to prevent storage exhaustion |

---
## 2. Workpath Platform Enablement Sequence

Before Workpath Apps can run on a device, the Workpath platform must be explicitly enabled by an administrator. The enablement is a one-time process: the administrator enables Workpath through the Embedded Web Server (EWS), the Dune firmware persists the setting to NVRAM and reboots the device. On the subsequent boot, `systemd` detects the flag, creates the necessary Android partitions, and starts the Workpath Linux Container (LXC). Once the container is active, the Dune platform verifies its status and marks the Workpath platform as **ENABLED**.

```plantuml
@startuml
title <font size=20><b>Workpath Enabling Sequence</b></font> \n\n

actor User
participant "Browser (EWS)" as EWS
participant "Dune MainApp" as Dune
database "NVRAM" as NVRAM
participant "systemd" as systemd
participant "Workpath Linux Container" as lxc

== Initialization ==
note over Dune #lightgreen
WorkpathEnabled: false
WorkpathPlatformStatus: AVAILABLE
end note
activate Dune
Dune -> Dune: Check product Workpath support\nSet WorkpathPlatformStatus = AVAILABLE

== Enable Workpath from EWS ==
EWS -> Dune: GET /cdm/solutionManager/v1/capabilities
activate EWS
Dune --> EWS: {"platformAvailability": "available"}

EWS -> Dune: GET /cdm/solutionManager/v1/configuration
Dune --> EWS: {"workpathEnabledConfig": false, "workpathEnabledState": false}

EWS -> User: Display Workpath "Enable" button
activate User
User -> EWS: Enable Workpath
deactivate User
EWS -> Dune: PATCH /cdm/solutionManager/v1/configuration\n{"workpathEnabledConfig": true}
deactivate EWS
activate Dune
Dune -> NVRAM: Set AndroidEnableEx = 1
activate NVRAM
NVRAM --> Dune
deactivate NVRAM
Dune -> Dune: Reboot
deactivate Dune
deactivate Dune

== On Next Boot ==
activate systemd
systemd -> NVRAM: Get AndroidEnableEx
activate NVRAM
NVRAM --> systemd
deactivate NVRAM
systemd -> systemd: Create Android partitions (/data, /cache)\nMount /lxc/workpath/rootfs
systemd --[#red]> lxc: **lxc-start**
activate lxc
systemd -> Dune: Start Dune MainApp process
activate Dune
deactivate systemd

Dune -> NVRAM: Get AndroidEnableEx
activate NVRAM
NVRAM --> Dune
deactivate NVRAM
note over Dune #lightgreen
WorkpathEnabled: true
WorkpathPlatformStatus: AVAILABLE
end note
Dune -> Dune: Check product Workpath support\nSet WorkpathPlatformStatus = AVAILABLE

Dune -> lxc: statusCheck
lxc --> Dune: {"status": "active", "platformVersion": "31.8"}
Dune -> Dune: Set WorkpathPlatformStatus = ENABLED\nSet WorkpathPlatformVersion = "31.8"
note over Dune #lightgreen
WorkpathEnabled: true
WorkpathPlatformStatus: ENABLED
end note

@enduml
```

---
## 3. Boot & Initialization Sequence

The startup sequence is **SystemApp-driven**: SystemApp initializes first to sync with Dune E2, then triggers the initialization of all other platform components by broadcasting `DEVICE_READY`.

1. **SystemApp Initialization** — On `BOOT_COMPLETED`, SystemApp retrieves the Workpath access token from the Dune CDM, then establishes a persistent WebSocket connection to the E2 Workpath Interop service. Once the WebSocket is active, SystemApp broadcasts `DEVICE_READY` to all platform components. After receiving `WORKPATH_SERVICE_INIT_COMPLETED` back from ServicesApp, it reports the platform status back to Dune (`workpathEnabledState: true`) over the WebSocket, which signals that the Workpath platform is ready and allows Workpath apps to be launched from the front panel.

2. **Other Pre-installed Apps Initialization** — On receiving `DEVICE_READY` from SystemApp, ServicesApp, PackageManagerApp, and LogDaemonApp each start their own initialization in parallel. During initialization, each app connects to the SystemApp WebSocket relay via AIDL Binder IPC to receive callbacks from Dune/E2. 


```plantuml
@startuml
title Workpath Startup Sequence
hide footbox
autonumber "<b>#."
skinparam sequenceArrowThickness 1
skinparam ArrowColor black
skinparam SequenceLifeLineBorderColor black
skinparam ParticipantBorderColor black
skinparam SequenceBoxBorderColor black
skinparam NoteBorderColor black
skinparam NoteFontSize 10


box "Dune FW" #f2f8fd
'participant "UI" as ui #66b3ff
participant "CDM/E2 Services" as e2s #66b3ff
participant "E2WorkpathInterop" as e2i #66b3ff
end box

box "Workpath System" #f2fcf2
participant "SystemApp" as systemApp #85e085
end box

box "Workpath Services" #f2fcf2
participant "[DeviceService]\nWebSocketCallbackService" as ws #85e085

'participant "[DeviceService]\nClients" as deviceServicesClients #85e085
participant "[DeviceService]\nDeviceReadyReceiver" as deviceReadyReceiver #85e085
participant "DeviceReadyWorker" as deviceReadyWorker

participant "[WorkpathService]\nServices" as serviceLets #85e085
end box

== Workpath SystemApp Initialization ==

    note left of e2s
    cdm/solutionManager/v1/configuration
    {
        "workpathEnabledState": "false",
        "workpathEnabledConfig": "true",
    }
    end note
    loop Bootup synchronization : retries until SystemApp is connected and clientInfo is received
    e2i <- systemApp: GET /cdm/2cWorkpathInterop/v1/clientInfo
        activate systemApp
        activate e2i
        e2i --> systemApp: return clientInfo
        deactivate e2i
        deactivate systemApp
    end

    e2s <- systemApp: POST /cdm/oauth2/v1/token (client_credentials)
        activate systemApp
        activate e2s
        e2s --> systemApp: return Workpath access token
        deactivate e2s
        deactivate systemApp


    e2i <- systemApp: POST /cdm/e2WorkpathInterop/v1/websocketConnection
        activate systemApp
        activate e2i
        e2i --> systemApp: return websocketPath
        deactivate e2i
        deactivate systemApp

    e2i <- systemApp: (Setup WebSocket) wss://{{IP}}{{websocketPath}}
        activate systemApp #2333c3
        activate e2i #2333c3
        e2i --> systemApp: connected
        e2i <- systemApp: preamble - Authorization: Bearer {{accessToken}}
        e2i --> systemApp: empty

    note over e2i, systemApp #2333c3
    <font color=white>WebSocket connection and setup completed between DuneFW and Workpath platform.
    <font color=white>WPInterop message communication begins.
    end note

    e2i -> systemApp: statusCheck
    e2s -> e2i : App channel setup messages
    e2i -> systemApp: App channel setup messages
    systemApp -> systemApp : enqueue WS messages
'systemApp -> systemApp: Event subscribe? (/cdm? Interop?)

== Workpath ServiceApp Initialization ==

systemApp -[#blue]> deviceReadyReceiver: <font color=blue>DEVICE_READY\n(device_ip, device_token)
    activate deviceReadyReceiver
    'deviceReadyReceiver -> deviceReadyReceiver: onReceive()\n
    create deviceReadyWorker

    deviceReadyReceiver --> deviceReadyWorker : 
    deactivate deviceReadyReceiver

activate deviceReadyWorker
deviceReadyWorker -> deviceReadyWorker : DeviceManagementService\n.initialize()
deviceReadyWorker -> e2s : CDM ServiceDiscovery
e2s--> deviceReadyWorker : ServiceDiscoveryResponse

deviceReadyWorker --> ws : Start service
activate ws #pink
ws -> systemApp: bindService\n(WebSocketCallbackService)
    activate systemApp #pink
    systemApp --> ws : onServiceConnected(IBinder)
    note over systemApp, ws  #pink
    AIDL-based Binder IPC transaction begins
    end note
    ws -> systemApp: addCallbacks for\nCdmPubSub, AppChannel, Gateway
    systemApp -> ws: dequeue WS messages
    activate ws
    ws -> ws : AppChannelMessageHandler\n.onReceive()\nenqueue messages if needed
    deactivate ws

deviceReadyWorker --> ws : Check AIDL connection
deviceReadyWorker -[#blue]> systemApp : <font color=blue>WORKPATH_SERVICE_INIT_COMPLETED</font>
deactivate deviceReadyWorker
activate systemApp
systemApp -> e2i: return statusCheck
note right of e2i #white
{
    "workpathPlatformStatus" : {
        "status" : "wsActive",
        "platformVersion" : "31.8"
    }
}
end note
e2i --> e2s : update state
note left of e2s
cdm/solutionManager/v1/configuration
{
    "workpathEnabledState": "true",
    "workpathEnabledConfig": "true",
}
end note
note left of e2s
Once "workpathEnabledState" is set to "true",
users can launch Workpath apps from the front panel.
end note

systemApp -[#blue]> serviceLets : <font color=blue>WORKPATH_SERVICE_READY</font>
activate serviceLets
deactivate systemApp
ws <- serviceLets : register Callback
activate ws
ws -> ws : dequeue WS messages
ws -> serviceLets : Callback
deactivate ws
deactivate serviceLets
' systemApp -> ws: dequeue WS messages
'     activate ws
'     ws -> ws : AppChannelMessageHandler\n.onReceive()
'     ws -> serviceLets : Callback
'     deactivate ws

' ui -> e2s : GET cdm/solutionManager/v1/configuration
' ui <-- e2s : 200 OK with configuration data
' note right of ui #white
' {
'   "version" : "1.0.0",
'   "workpathEnabledState" : "true",
'   "workpathEnabledConfig" : "true"
' }
' end note

@enduml
```

**Note**: For the LXC container start flow, refer to the internal Confluence page: [1-b. Android enable/disable on Tron — GS2.0 Tron Booting](https://rndwiki.inc.hpicorp.net/confluence/spaces/GUIC/pages/1260507290/1-b.+Android+enable+disable+on+Tron+-+GS2.0+Tron+Booting).

---
