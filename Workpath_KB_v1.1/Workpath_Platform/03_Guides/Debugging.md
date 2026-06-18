# Debugging & Troubleshooting — Dune Platform

## 1. Logging Strategy

Since direct ADB access is often restricted on production printers, **logging is the primary debugging tool** for platform developers.

### Platform Component Log Tags
Each Workpath component uses distinct log tag prefixes:

| Component | Tag Pattern | Examples |
|---|---|---|
| Workpath System | `[SSS]` | `[SSS][SystemService]`, `[SSS][BCRcv]`, `[SSS][Transport]`, `[SSS][SwitchRcv]` |
| Workpath Services (Let layer) | `{LetName}/` | `Scanlet/CP`, `Printlet/OXPPrintletCP`, `Scanlet/OPAdap` |
| Workpath Services (common) | `[WS]` | `[WS]SCAN`, `[WS]PRIN`, `[WS]JOB`, `[WS]AUTH` |
| DeviceServices Standard | `[WS]DSS/` | `[WS]DSS/DeviceMgmt`, `[WS]DSS/ScanJob`, `[WS]DSS/PrintJob` |
| DeviceServices Token | `[WS]DSS/` | `[WS]DSS/AppT` (AppTokenManager), `[WS]DSS` (UIContextTokenManager) |
| Package Manager | `[PM]` | `[PM][Install]`, `[PM][Verify]`, `[PM]CallbackService` |
| Link Launcher | `[Launcher]` | `[Launcher][Grid]`, `[Launcher][Home]` |

### Filtering in ADB
```bash
# View only Workpath System logs
adb logcat | grep SSS

# View Workpath Services Let layer logs (example: ScanLet)
adb logcat | grep "Scanlet"

# View DeviceServices / E2 client logs
adb logcat | grep "\[WS\]DSS"

# View token management (AppTokenManager / UIContextTokenManager)
adb logcat | grep "\[WS\]DSS/AppT\|\[WS\]DSS.*getUIContextToken"

# View all platform components together
adb logcat | grep -E "\[SSS\]|\[WS\]|Scanlet|Printlet|\[PM\]|\[Launcher\]"

# View E2 WebSocket transport
adb logcat | grep "\[SSS\]\[E2WebSocektListener\]"

# View context switching events
adb logcat | grep -E "\[SSS\]\[SwitchRcv\]|closeDisplay|showDisplay"
```

## 2. Retrieving Logs

### Via EWS (Embedded Web Server)
1. Open printer's IP address in a web browser
2. Navigate to **Tools** → **Troubleshooting** → **Advanced** → **Retrieve Diagnostic Data** and select "Create zipped debug information file" with "Generate Debug Data" to initiate diagnostics package generation

### Via USB
1. Insert FAT32 USB drive into printer
2. Navigate to Service Menu → Export Logs

### Via ADB (Development Mode)
```bash
# Live log stream
adb logcat

# Save to file
adb logcat > device_log.txt

# Clear and capture
adb logcat -c && adb logcat > fresh_log.txt
```

## 3. Using the Simulator

The **Workpath SDK Simulator** runs on your PC as an AVD (Android Virtual Device) and emulates the printer environment.

### Setup
1. Use `debugForSim` or `debug_sim` build variants
2. Install APKs to the AVD:
   ```bash
   adb install -r WorkpathServices-dune.apk
   adb install -r System-dune.apk
   ```
3. Trigger device ready:
   ```bash
   adb shell am broadcast \
     -a com.hp.workpath.intent.action.CALL_DEVICE_READY \
     -n com.hp.jetadvantage.link.system/.receivers.CDMCallReceiver
   ```

### Simulator vs. Hardware

```mermaid
graph LR
    subgraph "Simulator (AVD)"
        App_S[App]
        WPS_S[Workpath Services]
        DS_Sim["DeviceServices/Sim<br/>(Mock responses)"]
        
        App_S --> WPS_S --> DS_Sim
    end

    subgraph "Hardware (Real Device)"
        App_H[App]
        WPS_H[Workpath Services]
        DS_Std["DeviceServices/Standard<br/>(Real E2 API)"]
        E2_H[Dune E2 API]
        
        App_H --> WPS_H --> DS_Std --> E2_H
    end
```

| Aspect | Simulator | Hardware |
|---|---|---|
| **DeviceServices** | Sim (mock) | Standard (real) |
| **E2 API** | Mock responses | Real firmware |
| **ADB** | May be restricted | May be restricted |
| **Scanner** | Simulated | Real hardware |
| **Paper Jam** | Cannot test | Testable |
| **Speed** | Faster (no HW) | Real-world timing |

### Best Practice
Develop UI and logic on the Simulator → Test hardware integration on a real device.

## 4. Common Issues & Solutions

### "Service Not Found" / "ContentProvider Error"
```
Symptom: SsdkUnsupportedException during SDK initialization
```
- **Cause**: WorkpathServices APK not installed or not yet initialized.
- **Fix**: 
  1. Verify `WorkpathServices-dune.apk` is installed: `adb shell pm list packages | grep jetadvantage`
  2. Check that `WORKPATH_SERVICE_READY` broadcast has been sent. Inspect `[SSS]` logs for broadcast timing.
  3. On simulator, manually trigger: `adb shell am broadcast -a com.hp.workpath.intent.action.CALL_DEVICE_READY -n com.hp.jetadvantage.link.system/.receivers.CDMCallReceiver`

### "Permission Denied" / SecurityException
```
Symptom: SecurityException when accessing ContentProvider
```
- **Cause**: ContentProvider permission enforcement. Each Let’s ContentProvider requires `com.hp.jetadvantage.link.permission.SERVICES_PERMISSION`.
- **Debug**: Check the AndroidManifest of the calling app AND the WorkpathServices manifest for matching permission declarations.

### "E2 API Timeout"
```
Symptom: Scan/Print job hangs, no progress
```
- **Cause**: Device IP is wrong, E2 service not reachable, or discovery tree not built.
- **Fix**:
  1. Check `DUNE_HOST_IP` in SystemService logs: `adb logcat -s "[SSS]"`
  2. Verify E2 connectivity: try `curl https://{device_ip}/cdm/device/identity`
  3. Check `StandardDeviceManagementService` initialization logs.

### "Discovery Tree Empty"
```
Symptom: All Let operations fail with NullPointerException
```
- **Cause**: `DiscoveryServiceClient.discover()` failed.
- **Fix**: Check network connectivity between Android container and Dune FW.

### "Token Invalid" (401/403)
```
Symptom: E2 API returns 401 Unauthorized or 403 Forbidden
```
- **Cause**: `AppTokenManager` token expired or not obtained.
- **Fix**: Restart Workpath Services or check `AppTokenManager` logs.

### "WebSocket Disconnect"
```
Symptom: No async events (progress, status changes)
```
- **Cause**: E2 WebSocket connection dropped.
- **Fix**: Check `SystemService` logs: `adb logcat | grep "\[SSS\]\[SystemService\]"`

### "Launcher Not Showing"
```
Symptom: Black screen or wrong launcher after boot
```
- **Cause**: ComponentEnabledSetting failed or wrong launcher configured.
- **Fix**: Check SystemService launcher configuration logs:
  ```bash
  adb logcat -s "[SSS]" | grep "[Launcher]"
  ```

### "Boot Sync Incomplete"
```
Symptom: Wrong time, locale, or user session after boot
```
- **Cause**: `startDuneInfoThread()` failed to reach Dune FW.
- **Fix**: Check CDM endpoint reachability and `BootCompletedReceiver` log.

## 5. Diagnostic Commands (ADB)

```bash
# Check if all Workpath components are running
adb shell ps | grep -E "jetadvantage|workpath|packagemanager"

# Check if System Service is active
adb shell dumpsys activity services | grep SystemService

# List installed Workpath packages
adb shell pm list packages | grep -E "jetadvantage|workpath|hp"

# Force send DEVICE_READY (for testing)
adb shell am broadcast \
  -a com.hp.workpath.intent.action.CALL_DEVICE_READY \
  -n com.hp.jetadvantage.link.system/.receivers.CDMCallReceiver

# Test screen switch
adb shell am broadcast \
  -a com.hp.jetadvantage.link.SWITCH

# Check content providers (NOTE: Cannot query due to permission restrictions)
# adb shell content query --uri content://com.hp.workpath.system
```

## 6. Debug Flow Decision Tree (Platform)

```mermaid
graph TD
    Start["Platform Issue"] --> Q1{"Which component?"}
    Q1 -->|"System"| Q1S{"Boot issue?"}
    Q1S -->|Yes| A1S["Check BootCompletedReceiver logs<br/>Verify DEVICE_READY broadcast<br/>Check E2 Interop WebSocket"]
    Q1S -->|No| Q1S2{"Context switch issue?"}
    Q1S2 -->|Yes| A1S2["Check SwitchReceiver logs<br/>closeDisplay/showDisplay calls"]
    Q1S2 -->|No| A1S3["Check SystemService logs<br/>adb logcat -s SSS"]

    Q1 -->|"Services"| Q2S{"E2 API issue?"}
    Q2S -->|Yes| Q2S2{"Token error (401/403)?"}
    Q2S2 -->|Yes| A2S2["Check AppTokenManager logs<br/>Verify /cdm/e2WorkpathInterop/v1/appToken"]
    Q2S2 -->|No| A2S3["Check Discovery Tree<br/>Verify E2 GUN resolution"]
    Q2S -->|No| Q2S4{"ContentProvider error?"}
    Q2S4 -->|Yes| A2S4["Check Let ContentProvider logs<br/>Verify SERVICES_PERMISSION"]
    Q2S4 -->|No| A2S5["Check specific Let state machine"]

    Q1 -->|"Launcher / PM"| A3["Check PM install logs<br/>Verify signing certs<br/>Check launcher grid DB"]
```
