# Workpath Network Architecture (Dune)

This document describes the internal network architecture of the Workpath platform on the **Dune (FS6)** platform, which runs inside a Linux Container (LXC) on the device.

---

## 1. Network Topology Overview

The Workpath runtime runs inside a **Linux Container (LXC guest)** on the **Dune Linux host**. The host and the container are connected via `lxcbr0` (LXC bridge) and a veth pair (`vethVwhZB1@if9` ↔ `wp-mipiob0@if6`). The Workpath Platform can reach the host's internal bridge address (`156.152.79.233` = `fwprinter2` = `lxcbr0`) directly as the destination, enabling communication with Dune firmware services (e.g., E2 Interop REST API). Workpath Apps, however, are blocked from this destination — the container's **guest iptables OUTPUT chain** applies per-UID filtering and REJECTs traffic to `156.152.79.233` from non-whitelisted UIDs. Workpath Apps can still reach external networks; outbound packets are routed through the host's `eth0` interface where iptables MASQUERADE performs source NAT.

```plantuml
@startuml
skinparam defaultTextAlignment center
skinparam rectangleBorderColor #757575
skinparam rectangleFontSize 10
skinparam componentFontSize 10
skinparam ArrowFontSize 9
skinparam ArrowColor #555555
skinparam linetype ortho




rectangle "Dune Platform" {
    rectangle "Dune MainApp Processe" as DUNE #E3F2FD
    rectangle "Host Network Namespace" as HOST #EEF3FB {
        rectangle "eth0\n<device-ip>" as ETH0 #EEEEEE
        rectangle "lxcbr0\n(156.152.79.233/30)" as BR #EEEEEE
        rectangle "vethVwhZB1" as VETH_H #EEEEEE

        VETH_H -left--> BR : enslaved to
        BR .left.. ETH0 : routing / NAT
    }
    DUNE -down- HOST
}


rectangle "Linux LXC (Workpath)" {
    rectangle "Workpath\nPlatform" as WP #A5D6A7
    rectangle "Workpath\nApp" as APP #C8E6C9
    rectangle "Container Network Namespace\n(workpath)" as LXC #F1F8E9 {
        rectangle "iptables OUTPUT\n(per-UID REJECT)" as GFW #ffcdd29a
        rectangle "wp-mipiob0\n156.152.79.234/30" as MIPIOB0 #EEEEEE
    }
}

cloud "External\nNetwork" as EXT #EEEEEE

ETH0 -down- EXT
'DUNE -[hidden]down- HOST
VETH_H <-right-> MIPIOB0 : <<veth pair>>

WP -down-> GFW
APP -down-> GFW
GFW -down-> MIPIOB0


@enduml
```

The host and the container share a single `/30` subnet:

1. **Host ↔ Container** (`156.152.79.233`–`.234`, `/30`): `lxcbr0` (`.233`, bridge) on the host connects to `wp-mipiob0` (`.234`) in the container via a **veth pair** (`vethVwhZB1@if9` ↔ `wp-mipiob0@if6`).
2. The container network namespace has **only one kernel route**: `156.152.79.232/30 dev wp-mipiob0`. There is no default route. Traffic to destinations outside this subnet requires higher-layer handling (e.g., Android `netd` within the container).
3. External inbound access into the container is **not available** — only LDB port `5555` is forwarded via iptables DNAT if LDB is enabled on the device.

### 1.1 Network Interfaces

| Interface | Location | IP | Subnet | Role |
|---|---|---|---|---|
| **eth0** | Linux Host | {device-ip} | {subnet} | Physical NIC; external network access |
| **lxcbr0** | Linux Host | `156.152.79.233` | `/30` | LXC bridge; gateway address for the container |
| **vethVwhZB1** | Linux Host | — | — | veth host-side (if9), attached to `lxcbr0` |
| **wp-mipiob0** | LXC Container | `156.152.79.234` | `/30` | veth container-side (if6); communicates with host via `.233` |

---

## 2. Routing Tables

### 2.1 Host Routing Table

| Destination | Dev | Description |
|---|---|---|
| `default` via `15.26.176.10` | `eth0` | Default gateway to external network |
| `15.26.176.0/20` | `eth0` | External subnet (DHCP) |
| `156.152.79.232/30` | `lxcbr0` | LXC container subnet |

### 2.2 Container Routing Table

| Destination | Dev | Description |
|---|---|---|
| `156.152.79.232/30` | `wp-mipiob0` | Local subnet only — no default route |

The container has **no default route** at the kernel level. Traffic from the container to Dune firmware services or external networks relies on higher-layer handling within the Android runtime (e.g., `netd`), with host iptables MASQUERADE handling the NAT for outbound packets.

---

## 3. Host iptables NAT Rules

### PREROUTING

| In | Protocol | Source | Dest | Action | Purpose |
|---|---|---|---|---|---|
| `eth0` | TCP | any | any | DNAT → `156.152.79.234:5555` | LDB (ADB) inbound from external |
| `lxcbr0` | UDP | `156.152.79.234` | any dpt:53 | DNAT → `127.0.0.1:53` | Container DNS → host resolver |
| `lxcbr0` | UDP | `156.152.79.234` | any dpt:161 | DNAT → `127.0.0.1:161` | Container SNMP → host |

### POSTROUTING

| Out | Source | Action | Purpose |
|---|---|---|---|
| `eth0` / `eth1` / `wifi0` | `156.152.79.234` | MASQUERADE | Container → external network (outbound NAT) |

---

## 4. Access Control Summary

| Traffic Path | Allowed | Mechanism |
|---|---|---|
| Workpath Platform → (`156.152.79.233`) | Yes | Guest iptables OUTPUT: system UIDs ACCEPT |
| Workpath App → (`156.152.79.233`) | **No** | Guest iptables OUTPUT: REJECT (icmp-port-unreachable) |
| Workpath App / Platform → External network | Yes | MASQUERADE (POSTROUTING, out: eth0/eth1/wifi0) |
| Workpath container → host DNS/SNMP | Yes | PREROUTING DNAT → `127.0.0.1:53/161` |
| External → container port 5555 (LDB) | Yes (LDB mode only) | PREROUTING DNAT (in: eth0) |
| External → container (other ports) | **No** | No DNAT rules |


---

## 5. Traffic Flow Summary

```plantuml
@startuml
skinparam style strictuml
skinparam BoxPadding 10

box "External" #EEEEEE
participant "External\nNetwork" as EXT
end box

box "Dune Host" #E3F2FD
participant "eth0\n(iptables)" as HOST
participant "lxcbr0\n156.152.79.233" as BR
end box

box "LXC Container (Workpath)" #F1F8E9
participant "wp-mipiob0\n156.152.79.234" as MIPI
participant "iptables\nOUTPUT" as GFW
participant "Workpath\nPlatform" as WP
participant "Workpath\nApp" as APP
end box

== Workpath Platform → Dune Firmware (ALLOWED) ==

WP -> GFW : dst: 156.152.79.233 (system UID)
activate WP
activate GFW
GFW -> MIPI : ACCEPT
deactivate GFW
activate MIPI
MIPI -> BR : veth pair
activate BR
BR -> HOST : E2 Interop REST API
activate HOST
HOST --> BR : response
deactivate HOST
BR --> MIPI
deactivate BR
MIPI --> WP
deactivate MIPI
deactivate WP

== Workpath App → External Network (ALLOWED) ==
APP -> GFW : dst: external (app UID)
activate APP
activate GFW
GFW -> MIPI : ACCEPT
deactivate GFW
activate MIPI
MIPI -> BR : veth pair
activate BR
BR -> HOST : MASQUERADE (src → eth0 IP)
activate HOST
HOST -> EXT : outbound request
activate EXT
EXT --> HOST : response
deactivate EXT
HOST --> BR
deactivate HOST
BR --> MIPI
deactivate BR
MIPI --> APP
deactivate MIPI
deactivate APP

== Workpath App → Dune Firmware (BLOCKED) ==
APP -> GFW : dst: 156.152.79.233 (app UID)
activate GFW #FF8A80
GFW -->x APP : REJECT\n(icmp-port-unreachable)
deactivate GFW

== Dune Firmware → Workpath Platform (Inbound) ==

HOST -> BR : WebSocket push event
activate HOST
activate BR
BR -> MIPI : veth pair
activate MIPI
MIPI -> WP : deliver event
activate WP
deactivate WP
deactivate MIPI
deactivate BR
deactivate HOST

@enduml
```
