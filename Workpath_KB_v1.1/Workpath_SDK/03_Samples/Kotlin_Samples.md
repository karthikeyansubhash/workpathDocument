# Kotlin Samples

> **Audience**: Workpath SDK developers
> **Version**: HP Workpath SDK v1.6.3

---

## 1. Overview

The Kotlin samples are a **1:1 port** of the Java samples, rewriting the same 23 apps in idiomatic Kotlin. Functionality is identical; the differences lie in language features and modern Android development patterns.

---

## 2. Java vs Kotlin — Key Differences

| Aspect | Java Samples | Kotlin Samples |
|--------|-------------|----------------|
| File extension | `.java` | `.kt` |
| Gradle Plugin | `com.android.application` | `com.android.application` + `kotlin-android` |
| View access | `findViewById()` | **View Binding** (`ActivityMainBinding.inflate()`) |
| Async pattern | `ExecutorService` + `Handler` | **Kotlin Coroutines** (`lifecycleScope.launch`) |
| Thread switching | `handler.post(Runnable { ... })` | `withContext(Dispatchers.Main)` |
| Null safety | Manual null checks (NPE possible) | Safe call `?.`, `let`, `run` |
| Strings | `"Value:" + value` | `"Value:$value"` (string interpolation) |
| Singletons | `static` utility class | `object` declaration |
| Switch | `switch/case` | `when` expression |
| Lambdas | Anonymous inner class | Lambda `{ }` |
| Constants | `static final` | `companion object` |

---

## 3. Build Configuration Differences

### 3.1 Root build.gradle

```gradle
buildscript {
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20'  // Kotlin addition
    }
}
```

### 3.2 Module build.gradle

```gradle
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'                    // Kotlin plugin

android {
    // Same compileSdk/minSdk/targetSdk as Java
    
    buildFeatures {
        viewBinding true                           // View Binding enabled
    }

    kotlinOptions {
        jvmTarget = "11"                           // Kotlin JVM target
    }
}

dependencies {
    // Java common + Kotlin additions:
    implementation 'androidx.fragment:fragment-ktx:1.5.5'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.5.1'
}
```

---

## 4. Code Pattern Comparison

### 4.1 SDK Initialization

**Java:**
```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Handler handler = new Handler(Looper.getMainLooper());

executor.execute(() -> {
    try {
        Workpath.getInstance().initialize(context);
    } catch (SsdkUnsupportedException e) {
        handler.post(() -> handleError(e));
        return;
    }
    handler.post(() -> handleComplete());
});
```

**Kotlin:**
```kotlin
lifecycleScope.launch(Dispatchers.Default) {
    try {
        Workpath.getInstance().initialize(context)
    } catch (e: SsdkUnsupportedException) {
        withContext(Dispatchers.Main) { handleError(e) }
        return@launch
    }
    withContext(Dispatchers.Main) { handleComplete() }
}
```

### 4.2 View Binding

**Java:**
```java
Button mScanButton;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mScanButton = findViewById(R.id.scanButton);
    mScanButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) { startScan(); }
    });
}
```

**Kotlin:**
```kotlin
lateinit var mBindingActivityMain: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mBindingActivityMain.root)
    mBindingActivityMain.scanButton.setOnClickListener { startScan() }
}
```

### 4.3 Null Safety

**Java:**
```java
// Potential NPE
mContextRef.get().handleComplete();
```

**Kotlin:**
```kotlin
// Safe call — does not execute if null
mContextRef.get()?.run {
    handleComplete()
}
```

### 4.4 Logger Singleton

**Java:**
```java
public class Logger {
    private static final String TAG = "ScanSample";
    public static void d(String msg) {
        Log.d(TAG, msg);
    }
}
```

**Kotlin:**
```kotlin
object Logger {
    private const val TAG = "ScanSample"
    fun d(msg: String) {
        Log.d(TAG, msg)
    }
}
```

---

## 5. Coroutines Deep-Dive

The key architectural difference in Kotlin samples is the use of **Kotlin Coroutines**:

### 5.1 Dispatchers Used

| Dispatcher | Usage |
|-----------|-------|
| `Dispatchers.Default` | SDK API calls (background) |
| `Dispatchers.Main` | UI updates |

### 5.2 InitializationTask Pattern

```kotlin
class InitializationTask(activity: MainActivity) {
    private val mContextRef = WeakReference(activity)

    suspend fun execute() {
        val context = mContextRef.get() ?: return

        // Runs in background (caller specifies Dispatchers.Default)
        try {
            Workpath.getInstance().initialize(context)
        } catch (e: SsdkUnsupportedException) {
            withContext(Dispatchers.Main) {
                mContextRef.get()?.handleError(e)
            }
            return
        }

        val result = Result()
        val caps = ScannerService.getCapabilities(context, result)

        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                handleComplete(caps)
            }
        }
    }
}

// Called from Activity
lifecycleScope.launch(Dispatchers.Default) {
    InitializationTask(this@MainActivity).execute()
}
```

### 5.3 Scope Function Usage

| Scope Function | Example Usage |
|---------------|---------------|
| `let` | `caps?.let { updateUI(it) }` |
| `run` | `mContextRef.get()?.run { handleComplete() }` |
| `apply` | Builder chaining |
| `also` | Passing values with logging |

---

## 6. Package Structure

Java and Kotlin samples use identical package naming:

```
com.hp.workpath.sample.<samplename>
```

e.g., `com.hp.workpath.sample.scansample`, `com.hp.workpath.sample.printsample`

---

## 7. Gradle Properties

```properties
android.enableJetifier=true      # Automatic Support → AndroidX conversion
android.useAndroidX=true         # Use AndroidX
```

---

## 8. Extension — GoogleSigninSample (Kotlin)

The GoogleSigninSample extension is a **separate Gradle project**, and the Kotlin version uses an **older Kotlin plugin** than ExampleAPIServices:

| Setting | ExampleAPIServices | ExampleExtensions |
|---------|-------------------|-------------------|
| Kotlin Plugin | 1.8.20 | 1.6.20 |

> Extensions are supplementary samples managed separately from ExampleAPIServices.

---

## 9. SDK Developer Guidelines

Key points when developing/maintaining Kotlin samples:

1. **Synchronize features with Java samples** — Update both when adding new APIs
2. **Maintain Coroutines pattern** — Do not use `ExecutorService`
3. **Use View Binding** — Do not use `findViewById()`
4. **Safe call pattern** — Minimize use of `!!` (non-null assertion)
5. **Use Kotlin idioms** — `when`, scope functions, string interpolation
6. **Keep identical package names** — Maintain `com.hp.workpath.sample.*` consistent with Java

---

*→ Next: [Extensions](Extensions.md)*
