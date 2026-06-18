# HP Workpath Platform Knowledge Base — Dune (FS6)

Internal documentation of the **HP Workpath platform on Dune (Future Smart 6)** firmware, covering architecture, source code structure, communication protocols, build processes, and platform debugging.

> **Target Audience**: AI coding agents, Workpath **Platform** developers, Workpath **SDK** developers  
> **NOT for**: Solution app developers (3rd-party). For solution app development, refer to the SDK Javadoc and sample apps.  
> **Workpath API Version**: 1.6.x  
> **Firmware Platform**: Dune (Future Smart 6)  
> **Android Runtime**: Android 12 (API 31)  
> **Source**: All content is grounded in the source code repositories, AndroidManifest, build.gradle, and README files.

---

## 1. Concepts

Foundational architecture and terminology for Workpath development on Dune.

- **[Architecture](01_Concepts/Architecture.md)** — Platform components, communication protocols (E2/CDM/WebSocket), SDK architecture, request flow, token management, boot sequence, DeviceServices abstraction.
- **[Glossary](01_Concepts/Glossary.md)** — Definitions of Workpath, Dune, and SDK terms. Includes Let module listing with ContentProvider authorities.
- **[Security Model](01_Concepts/Security_Model.md)** — Platform signing, Android permissions (all protection levels), E2 token-based access, HPK verification, protected broadcasts.

## 2. Components

Source code structure and internal components of each platform repository.

- **[Workpath System](02_Components/Workpath_System.md)** — `System-dune-master`: Services, Receivers, Providers, WebSocket handlers, CDM models. Boot orchestration, screen switching, launcher management.
- **[Workpath Services](02_Components/Workpath_Services.md)** — `workpath-services-dune-master`: 32 modules, 17 Let modules, DeviceServices, ContentProvider IPC pattern, manifest details.
- **[DeviceServices Layer](02_Components/DeviceServices.md)** — Interfaces (28) / Standard (23+) / Sim / Clients: `perform()` + `E2call` pattern, token management, Discovery Tree, ScanLet adapter classes.
- **[Link SDK (WorkpathLib)](02_Components/Link_SDK.md)** — `linksdklib-master`: Library modules, API packages, ContentProvider proxy, SDK distribution package structure.
- **[Package Manager](02_Components/Package_Manager.md)** — `packagemanager-dune-master`: HPK packaging, CpkLib, ContentProviders, permissions (8 defined), HPK Tool.
- **[Log Daemon](02_Components/Log_Daemon.md)** — `LogDaemon-dune-master`: LogcatService, log collection and persistence.

## 3. Guides

Practical development guides for building and working with the Workpath platform.

- **[Building & Development](03_Guides/Building_and_Development.md)** — Build variants, signing, version management, CI/CD, dependency matrix, development flow.
- **[Debugging & Troubleshooting](03_Guides/Debugging.md)** — Log tags, ADB filtering, Simulator setup, common issues, diagnostic commands.
- **[App Lifecycle & Boot](03_Guides/App_Lifecycle.md)** — Boot sequence (5 phases), broadcast ordering, screen switching, Let readiness events.
- **[App Installation](03_Guides/App_Installation.md)** — Installation methods (ADB, EWS, WXP, USB), HPK packaging, Package Manager flow.
- **[Hardware Control](03_Guides/Hardware_Control.md)** — Let implementation patterns: ContentProvider dispatch, adapter pattern, E2 call chain, IPP print, adding a new Let.

## 4. References

Quick-lookup tables for APIs, errors, permissions, and broadcasts.

- **[E2/CDM API Endpoints](04_References/API_Signatures.md)** — Verified E2 endpoints, service GUNs, CDM endpoints, WebSocket message types, DeviceServices interface signatures.
- **[Error Handling](04_References/Error_Codes.md)** — Exception classes (`OXPdHttpRequestException`, `BoundDeviceException`), `perform()` error handling, error propagation chain.
- **[Permissions](04_References/Permissions.md)** — All Workpath-defined permissions (SDK, System, Package Manager, LogDaemon) with protection levels and feature requirements.
- **[Broadcast Actions & Intents](04_References/Broadcast_Actions.md)** — All broadcast actions, receivers, protected broadcasts, boot sequence flow.
- **[Repository Map](04_References/Repository_Map.md)** — Repository list with versions, build configs, local paths, and component dependency graph.

---

*All content in this KB is derived from the source code, configuration files, and documentation in the Knowledge_source directory. Facts are attributed to specific source files where applicable.*
