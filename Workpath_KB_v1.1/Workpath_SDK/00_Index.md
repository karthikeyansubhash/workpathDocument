# Workpath SDK Library — Knowledge Base Index

> **Audience**: Workpath SDK developers (responsible for SDK Library development and distribution)
> **SDK Version**: HP Workpath SDK v1.6.3
> **Platform**: Dune (FS6) — Android 12

*This index serves as the navigation map for Workpath SDK developers to understand the SDK Library package components, API surface, samples, and tools that are developed and distributed as part of each SDK release.*

---

## Scope

[Workpath Platform KB](../Workpath_Platform/00_Index.md) covers the Workpath Platform and Dune firmware development needed for the **runtime execution** of APIs defined in WorkpathLib.

This KB (Workpath SDK Library) covers the **development and distribution of the SDK Library itself**, including all deliverables shipped in the SDK release package: Library, API Docs, Samples, Tools, Simulator, and Documentation.

---

## Section 1 — Overview

Describes the overall structure and release composition of the SDK package.

| Document | Description |
|----------|-------------|
| [SDK Package Overview](01_Overview/SDK_Package_Overview.md) | Purpose, target users, and components of the SDK release package |
| [Release Package Structure](01_Overview/Release_Package_Structure.md) | Package directory structure, file inventory, and versioning |

---

## Section 2 — Library & API

Provides detailed description of the WorkpathLib.aar library and the full API surface.

| Document | Description |
|----------|-------------|
| [WorkpathLib](02_Library/WorkpathLib.md) | WorkpathLib.aar structure, dependencies, and integration methods |
| [API Surface](02_Library/API_Surface.md) | 34 packages, 150+ classes, 100+ enums — complete API catalog |
| [API Patterns](02_Library/API_Patterns.md) | Initialization, Job submission, Observer, Builder, and other key usage patterns |

---

## Section 3 — Sample Applications

Covers the Java/Kotlin sample apps and Extension samples.

| Document | Description |
|----------|-------------|
| [Sample Apps Overview](03_Samples/Sample_Apps_Overview.md) | Complete list of 23 sample apps, functional categories, and build configuration |
| [Java Samples](03_Samples/Java_Samples.md) | Java sample details — source structure, API usage patterns, async handling |
| [Kotlin Samples](03_Samples/Kotlin_Samples.md) | Kotlin sample details — Coroutines, ViewBinding, and differences from Java |
| [Extensions](03_Samples/Extensions.md) | GoogleSigninSample and other Extension samples — purpose and implementation |

---

## Section 4 — Tools & Documentation

Covers the tools and documentation guides distributed with the SDK.

| Document | Description |
|----------|-------------|
| [HPK Tool](04_Tools/HPK_Tool.md) | HPKTool purpose, usage, and APK-to-HPK packaging |
| [Simulator](04_Tools/Simulator.md) | Workpath SDK Simulator installation, configuration, and development usage |
| [Documentation Guide](04_Tools/Documentation_Guide.md) | SDK User Guide, API Docs (Javadoc), and Feature Overview document structure |

---

*Workpath_SDK_LIB_KB — written based on v1.6.3*
