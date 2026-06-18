# Java Samples

> **Audience**: Workpath SDK developers
> **Version**: HP Workpath SDK v1.6.3

---

## 1. Project Structure

The Java sample project is organized as a **multi-module Gradle project**:

```
Samples/ExampleAPIServices/source/
├── build.gradle           ← Root build file (AGP 7.4.2)
├── settings.gradle        ← 24 module includes
├── gradle.properties      ← Project settings
├── debug.keystore         ← Debug signing key
├── gradle/wrapper/
├── WorkpathLib/           ← AAR wrapper module
│   ├── build.gradle
│   └── WorkpathLib.aar
├── ScanSample/
│   ├── AndroidManifest.xml
│   ├── build.gradle
│   ├── src/               ← Java source
│   ├── res/               ← Resources
│   └── assets/
├── PrintSample/
│   └── ...
└── ... (23 app modules)
```

### settings.gradle

```gradle
include ':WorkpathLib'
include ':AccessorySample'
include ':AccessoryAgentSample'
include ':AccessoryServiceSample'
include ':AccessSample'
include ':AttestationSample'
include ':AuthenticationAgent'
include ':AuthenticationAgentWithPrePrompt'
include ':AuthorizationSample'
include ':ConfigSample'
include ':CopySample'
include ':DeviceInfoSample'
include ':DeviceUsageSample'
include ':DeviceEventSample'
include ':EmailSample'
include ':EventNotificationSample'
include ':PrintSample'
include ':ScanSample'
include ':LauncherSample'
include ':MassStorageSample'
include ':MultiLanguageSample'
include ':StatisticsSample'
include ':SuppliesSample'
include ':WebServiceSample'
```

---

## 2. Common Module Build Configuration

Base build.gradle structure for each sample module:

```gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 31

    defaultConfig {
        applicationId "com.hp.workpath.sample.<samplename>"
        minSdkVersion 31
        targetSdkVersion 31
        versionCode 17
        versionName '1.6.3 (20251111)'
        multiDexEnabled true
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }

    signingConfigs {
        debug {
            storeFile file("${rootProject.projectDir}/debug.keystore")
            storePassword 'android'
            keyAlias 'androiddebugkey'
            keyPassword 'android'
        }
    }

    sourceSets {
        main {
            manifest.srcFile 'AndroidManifest.xml'
            java.srcDirs = ['src']
            res.srcDirs = ['res']
            assets.srcDirs = ['assets']
        }
    }
}

dependencies {
    implementation project(':WorkpathLib')
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'androidx.multidex:multidex:2.0.0'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.preference:preference:1.2.0'
}
```

---

## 3. Async Execution Pattern

All Java samples use the `ExecutorService` + `Handler` pattern (AsyncTask is deprecated):

```java
public class InitializationTask {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final WeakReference<MainActivity> mContextRef;

    public InitializationTask(MainActivity activity) {
        mContextRef = new WeakReference<>(activity);
    }

    public void execute() {
        executor.execute(() -> {
            Context context = mContextRef.get();
            if (context == null) return;

            // Background — SDK initialization
            try {
                Workpath.getInstance().initialize(context);
            } catch (SsdkUnsupportedException e) {
                handler.post(() -> mContextRef.get().handleError(e));
                return;
            }

            // Background — query capabilities
            Result result = new Result();
            ScanAttributesCaps caps = ScannerService.getCapabilities(context, result);

            // UI thread — deliver results
            handler.post(() -> {
                MainActivity activity = mContextRef.get();
                if (activity != null) {
                    activity.handleComplete(caps);
                }
            });
        });
    }
}
```

---

## 4. Key Sample Deep-Dives

### 4.1 ScanSample

**Purpose**: Demonstrates the full scan job lifecycle

**Core flow**:
1. `Workpath.getInstance().initialize(context)`
2. `ScannerService.isSupported(context)` / `JobService.isSupported(context)` check
3. `ScannerService.getCapabilities(context, result)` — query capabilities
4. `ScanAttributes.MeBuilder(caps)` — build attributes (destination, color, resolution, format, margins, duplex)
5. `ScanletAttributes.Builder()` — task attributes (settingsUI, allowMultipleScan)
6. `ScannerService.submit(context, attributes, taskAttrs)` — returns rid
7. `JobService.AbstractJobletObserver` — onProgress/onComplete/onFail/onCancel
8. `JobService.cancelJob(context, jobId)` — cancellation

**Supported Destinations**: Email, FTP, HTTP, Network Folder, USB, Me (local)

### 4.2 PrintSample

**Purpose**: Demonstrates printing from various sources

**Print sources**:
- `PrintFromStorageBuilder(uri)` — device storage
- `PrintFromHttpBuilder(uri)` — HTTP URL
- `PrintFromUsbBuilder(path)` — USB device
- `PrintFromStreamBuilder(inputStream)` — in-app stream

**Print attributes**: duplex, color, paper size/type/source, staple, collate, orientation, quality, output bin, page ranges, finishings

**Batch print**: `PrinterService.submit(context, List<PrintAttributes>, taskAttrs)`

### 4.3 CopySample

**Purpose**: Demonstrates copy + stored job management

**Key features**:
- Standard copy: `CopierService.submit()`
- Stored copy: `StoreCopyBuilder` + `RetentionMode.STORE`
- Stored job management: enumerate, release, delete
- Stamp options, shifts, margins, credentials

### 4.4 WebServiceSample

**Purpose**: Demonstrates the Link Bus web service callback pattern

**Structure**:
- Extends `AbstractWebServices`
- Implements `Callback` interface: `authenticated()`, `get()`, `post()`, `put()`, `delete()`
- HTTP request/response handling

### 4.5 EventNotificationSample

**Purpose**: Demonstrates receiving system broadcast events

**Received events** (API 9):
- `SLEEP` / `WAKE_UP` — `SleepWakeUpReceiver`
- `SIGN_IN` / `SIGN_OUT` — `SignInOutReceiver`
- `JOB_COMPLETED` — `JobCompletedReceiver`
- `CONFIG_CHANGED` — `ConfigChangedReceiver`

> Runs as a Foreground Service to receive events even when the app is in the background.

---

## 5. Sample-Specific Dependencies

Some samples use additional dependencies:

| Sample | Additional Dependencies |
|--------|------------------------|
| CopySample | `com.google.code.gson:gson:2.11.0` |
| AuthorizationSample | ViewBinding enabled |
| ConfigSample | `androidx.security:security-crypto` (EncryptedFile, MasterKeys) |
| LauncherSample | HID AccessoryService addition |

---

## 6. View Access Pattern

Java samples use the traditional `findViewById()` pattern:

```java
public class MainActivity extends AppCompatActivity {
    private Button mScanButton;
    private TextView mStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScanButton = findViewById(R.id.scanButton);
        mStatusText = findViewById(R.id.statusText);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }
}
```

---

## 7. SDK Developer Checklist (Java Samples)

Tasks to perform for each sample at release time:

- [ ] Reflect new API changes
- [ ] Build source (`gradlew assembleDebug`)
- [ ] Extract APK → copy to `apks/` directory
- [ ] Convert with HPKTool → copy to `hpk/` directory
- [ ] Update versionName/versionCode
- [ ] Verify debug.keystore validity

---

*→ Next: [Kotlin Samples](Kotlin_Samples.md)*
