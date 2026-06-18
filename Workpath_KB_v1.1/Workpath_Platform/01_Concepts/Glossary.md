# HP Workpath Platform — Glossary

This glossary defines the key terms and abbreviations used in the Workpath Platform architecture documentation for Dune (FutureSmart 6).

---

## A

**AIDL (Android Interface Definition Language)**
*Category: Android · Workpath*
An Android IPC mechanism used to define programming interfaces that allow the client and service to communicate across processes.

**Android Java API**
*Category: Android*
The standard Android framework APIs provided by AOSP (e.g., Activity, Intent, Service, BroadcastReceiver). Used by Workpath Apps for conventional Android application behavior, independent of the Workpath-specific API surface.

**Android Runtime (ART)**
*Category: Android*
The managed runtime environment in which Android applications execute. ART compiles app bytecode into native instructions and handles garbage collection, threading, and other runtime concerns.

**AOSP (Android Open Source Project)**
*Category: Android*
The open-source base of the Android operating system. The Workpath Platform runs on Android 12 (AOSP) as its application runtime within the LXC container.

**App-to-App Launch**
*Category: Workpath*
A mechanism by which one installed Workpath App explicitly launches another installed app using standard Android Intents.

---

## B

**Bearer Token**
*Category: Workpath · Dune*
An HTTPS authentication token included in API request headers to authorize access. All communication between the Workpath Platform and the Dune Platform (CDM, E2, E2 Workpath Interop) uses bearer-token authorization.

**Binder**
*Category: Android · Workpath*
A remote procedure call (RPC) mechanism in Android that allows a client process to remotely invoke a function on a server process. The Workpath Platform uses Binder to enforce UID/PID-based caller identity when apps call Workpath APIs.

**Broadcast-Driven Launch**
*Category: Workpath*
An app launch mechanism in which a Workpath App registers a `BroadcastReceiver` for one or more predefined Workpath broadcast events. The Workpath Platform dispatches a matching broadcast to start the app.

---

## C

**CDM (Common Data Model)**
*Category: Dune*
A public firmware API layer on the Dune Platform that provides access to common device resources, configuration data, installed solution licenses, and device-level information over HTTPS REST / JSON.

**cgroups (Control Groups)**
*Category: Android*
A Linux kernel feature used to limit and isolate resource usage of processes. The LXC container uses cgroups to enforce a 1.25 GB RAM cap on the Workpath runtime.

**Container Isolation**
*Category: Android · Dune*
The security boundary enforced by LXC, SELinux, and cgroups that prevents the Android runtime from directly accessing the host Linux system or other containers.

**ContentProvider**
*Category: Android · Workpath*
A data-sharing mechanism in Android that provides SQL-like APIs and enables data exchange among processes. In the Workpath Platform, ContentProvider is used as the primary IPC channel for synchronous request–response communication between Workpath Apps and the Workpath Platform Services.

---

## D

**Dune**
*Category: Dune*
The native firmware platform underlying HP FutureSmart 6 (FS6) enterprise printer products. It manages device core functions (print engine, scan pipeline, network stack) through Dune Core and exposes extensibility APIs through Extensibility2 (E2). Externally branded as HP FutureSmart 6.

---

## E

**E2 (Extensibility2 / OXPd2)**
*Category: Dune*
The public device extensibility framework on the Dune Platform. E2 (also known as OXPd2) provides a REST-based API with JSON data format, built on top of the Common Data Model (CDM) and Extensibility architecture. It offers a common framework for all solution platforms and deployment models. E2 replaces the WS*-XML and REST-XML based API data models used by prior OXPd releases.

**E2 Solution Manager**
*Category: Dune*
A service running within the Dune Platform that acts as the front-end entry point for app installation. It validates and unpacks solution bundles, manages E2 resources, and coordinates with the Workpath `PackageManagerApp` to install APKs.

**E2 Workpath Interop**
*Category: Dune*
A private internal API surface on the Dune Platform developed exclusively for Workpath-specific firmware integration. Used only when no suitable standard E2 or CDM API exists.

**E2 Workpath Interop WebSocket**
*Category: Dune*
A private asynchronous event channel through which the Dune Platform pushes callback notifications to the Workpath Platform over WebSocket.

**EWS (Embedded Web Server)**
*Category: Dune*
The built-in web interface of the printer, accessible via browser, through which apps can be sideloaded onto the device.

---

## F

**FutureSmart 6 (FS6)**
*Category: Dune*
See **Dune**.

**FutureSmart 5 (FS5)**
*Category: JOLT*
See **Jolt**.

---

## H

**HP App Center**
*Category: Cloud*
HP's official distribution channel for Workpath Apps. Apps are published here after passing the V&V process and HP signing.

---

**.hpk (HP Package)**
*Category: Workpath · Packaging*
The Workpath app bundle format used on Jedi (FS4) and Jolt (FS5). Contains the APK and associated metadata. Not compatible with Dune; see `.hpk2`.

---

**.hpk2 (HP Package 2)**
*Category: Workpath · Packaging*
The Workpath app bundle format introduced for Dune (FS6). Replaces `.hpk` and is not backward-compatible with it. The APK inside the bundle is reusable from `.hpk` without a rebuild. Refer https://hp.sharepoint.com/:p:/s/Dune/EdMxPZT2-KZIqTC6HGDRRgwBeocdCuIX6fROfY8ayiClEA for more details.

---

## I

**Intent**
*Category: Android*
A message-forwarding mechanism in Android IPC. A system service forwards a message to its proper receivers based on intent-filtering policies. In the Workpath Platform, Intents (Broadcasts) are used for asynchronous communication such as job submission and status callbacks between Workpath Apps and the Workpath Platform.

**IPC (Inter-Process Communication)**
*Category: Android · Workpath*
The mechanism by which separate processes communicate. Within the LXC container, the Workpath SDK Library proxies all Workpath API calls to the Workpath Platform's `ServicesApp` via Android IPC (Binder, Content Providers, Broadcast Intent, AIDL).

---

## J

**JetAdvantage Link**
*Category: Workpath*
The former name of the HP Workpath platform. The product was rebranded from "HP JetAdvantage Link" (also referred to as "HP Link") to "HP Workpath" to better reflect its expanded capabilities and positioning.

**Jolt**
*Category: JOLT*
Jedi on Linux on Tron. HP FutureSmart 5 (FS5) firmware platform for enterprise printing products. Runs the legacy Jedi platform's managed code (C#) on Linux using the Mono runtime on the .NET platform.

---

## L

**LaserJet Debug Bridge**
*Category: Workpath*
Formerly called "Link Debug Bridge" (when "HP Workpath" was branded as "HP Link"). A development tool used by third-party solution/app developers to enable advanced debugging capabilities on Dune devices. When enabled, it bypasses HP signing verification and license enforcement, allowing unsigned and unlicensed apps to be installed and tested during development. Refer https://rndwiki.inc.hpicorp.net/confluence/spaces/OPSExtensibility/pages/1341482074/LaserJet+Debug+Bridge+LDB.

**LXC (Linux Container)**
*Category: Android*
An OS-level virtualization technology used to run the Android 12 runtime in an isolated environment on top of the host Linux kernel. The LXC container provides namespace isolation, filesystem isolation, network isolation, and resource control via cgroups.

---

## O

**ObserverService-Driven Launch**
*Category: Workpath*
An app launch mechanism in which the Workpath Platform starts a registered observer service within a Workpath App in response to a relevant platform event, as defined by the Workpath API.

**OXPd (Open eXtensibility Platform for devices)**
*Category: Dune*
An enterprise software development platform for creating device extensions and front-panel document workflow applications based on standard web service and web application protocols. OXPd centralizes third-party code (called a Solution) on a Solution Server connected to the same network. The device administrator configures devices on the network to work with a Solution Server to provide additional functionality.

**OXPd2**
*Category: Dune*
The newest generation of OXPd, based on the latest web standards and best practices. OXPd2 is available only on the newest devices running FutureSmart 6 and later. See **E2 (Extensibility2 / OXPd2)**.

---

## P

**PackageManagerApp**
*Category: Workpath*
See **Workpath PackageManagerApp**.

---

## S

**SELinux (Security-Enhanced Linux)**
*Category: Android*
A Linux kernel security module implementing Mandatory Access Control (MAC). Within the Workpath environment, SELinux enforces access control policies inside the Android container.

**ServicesApp**
*Category: Workpath*
See **Workpath ServicesApp**.

**Solution Access Token**
*Category: Dune*
A bearer token type used when the Workpath Platform calls E2 Agent services on behalf of a Workpath App.

**SystemApp**
*Category: Workpath*
See **Workpath SystemApp**.

---

## U

**UI Context Token**
*Category: Dune*
A bearer token type required when accessing E2 services that require UI screen ownership, such as creating a Scan Job.

**UI-Initiated Launch**
*Category: Workpath*
An app launch mechanism in which the user selects the app's icon on the device's control panel touchscreen.

---

## V

**V&V (Verification and Validation)**
*Category: Workpath · Cloud*
HP's process for reviewing and approving third-party Workpath Apps before distribution. Apps that pass V&V are signed with an HP key and published through the HP App Center.

---

## W

**Workpath Access Token**
*Category: Workpath · Dune*
A bearer token type used when the Workpath Platform consumes CDM or E2 APIs for its own internal operations (not on behalf of an app).

**Workpath App**
*Category: Workpath*
A third-party or in-house Android application that runs within the LXC container and uses the Workpath SDK to access device capabilities. Must pass HP's V&V process and be signed for production deployment.

**Workforce Experience Platform (WXP)**
*Category: Cloud*
HP's cloud-based management portal for Workpath-enrolled devices. Through WXP, administrators can remotely deploy, update, or remove Workpath Apps across a fleet of printers. It succeeds and replaces the earlier HP Command Center (HPCC).

**Workpath Framework**
*Category: Workpath*
The AOSP-based Android OS layer within the Workpath Platform that provides the Android Runtime (ART) and the Android Java API framework to Workpath Apps.

**Workpath PackageManagerApp**
*Category: Workpath*
A pre-installed Workpath platform application responsible for managing app installation, removal, metadata persistence, and coordination with the Dune E2 SolutionManager.

**Workpath Platform**
*Category: Workpath*
The shared Android-based runtime environment inside the LXC container that hosts Workpath Apps and provides platform services including the Workpath Framework and Workpath Platform Services.

**Workpath SDK**
*Category: Workpath*
The software development kit distributed to third-party developers. Contains the Workpath SDK Library (as an AAR/JAR), API documentation in JavaDoc format, and development tools.

**Workpath SDK Library**
*Category: Workpath*
A Java library included in the Workpath SDK and bundled into Workpath Apps at build time. It exposes the Workpath API surface to solution code and proxies all calls to the Workpath Platform via IPC. Contains no business logic of its own.

**Workpath SDK API**
*Category: Workpath*
The set of categorized Java service interfaces exposed through the Workpath SDK Library, covering device capabilities such as `AccessService`, `DeviceService`, `ScannerService`, `PrinterService`, and `CopierService`, among others. All APIs are documented in JavaDoc format.

**Workpath Platform Services**
*Category: Workpath*
A set of pre-installed Workpath platform core applications (`SystemApp`, `PackageManagerApp`, `ServicesApp`) that manage the app lifecycle, package installation, and Workpath API service exposure.

**Workpath ServicesApp**
*Category: Workpath*
A pre-installed Workpath platform application that hosts Workpath API services (e.g., `AccessService`, `DeviceService`, `ScannerService`, `PrinterService`, `CopierService`) over Android IPC, making them accessible to Workpath Apps.

**Workpath SystemApp**
*Category: Workpath*
A pre-installed Workpath platform application responsible for platform startup, app launch and reset, UI screen transitions between the Workpath and Dune interfaces, and license enforcement.

**WXP**
*Category: Cloud*
See **Workforce Experience Platform**.
