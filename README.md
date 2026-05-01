<div align="center">

# 🌍 AutoLocale

### Stop writing `strings.xml` by hand. Your code is your source of truth.

[![JitPack](https://img.shields.io/badge/JitPack-1.0.0--alpha-brightgreen?style=flat-square)](https://jitpack.io)
[![Jetpack Compose](https://img.shields.io/badge/Compose-1.5%2B-green?style=flat-square&logo=jetpack-compose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/license-MIT-f59e0b?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-059669?style=flat-square&logo=android)](https://www.android.com/)
[![API](https://img.shields.io/badge/API-26%2B-blue?style=flat-square)](https://android-arsenal.com/api?level=26)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](CONTRIBUTING.md)

<br/>

**⭐ If this saves you hours of localization work, star the repo — it helps others find it.**

<br/>

</div>

---

## 🤦 The problem with Android localization today

Every Android developer has been there:

```kotlin
🤦 "Let me manually copy every string into strings.xml..."
🤦 "Now I need to create values-ar/, values-fr/, values-ur/ and translate each one by hand."
🤦 "The design changed again. Update strings.xml, update translations, recheck keys..."
🤦 "I forgot to add this string to the Arabic file. Build passed but Arabic is broken."
```

Android localization is a **manual, error-prone, never-ending chore.** AutoLocale fixes this permanently.

---

## ✨ What AutoLocale Does

Write your UI normally with hardcoded strings:

```kotlin
// Before — what you write naturally
Text("Welcome to our app")
Text("Please login to continue")
Text("Forgot your password?")
```

Run one Gradle command. AutoLocale handles everything else:

```kotlin
// After — what your code becomes automatically
Text(stringResource(R.string.al_welcome_to_our_app))
Text(stringResource(R.string.al_please_login_to_continue))
Text(stringResource(R.string.al_forgot_your_password))
```

And your `strings.xml` files across all languages — generated and translated automatically:

```xml
<!-- values/strings.xml -->
<string name="al_welcome_to_our_app">Welcome to our app</string>

<!-- values-ar/strings.xml -->
<string name="al_welcome_to_our_app">مرحباً بك في تطبيقنا</string>

<!-- values-fr/strings.xml -->
<string name="al_welcome_to_our_app">Bienvenue dans notre application</string>

<!-- values-ur/strings.xml -->
<string name="al_welcome_to_our_app">ہمارے ایپ میں خوش آمدید</string>
```

**Zero manual translation. Zero missing keys. Zero drift between code and resources.**

---

## 🏗️ Architecture — Two Modules

AutoLocale is split into two independent parts:

```
autolocale/
├── autolocale-plugin/     → Gradle Plugin (build-time magic)
│   ├── Scanner            → Finds hardcoded Text("...") in your .kt files
│   ├── KeyGenerator       → Creates unique, collision-safe XML keys
│   ├── XmlManager         → Writes & translates strings.xml files
│   └── SourceTransformer  → Rewrites your source code safely
│
└── autolocale-runtime/    → Runtime Library
    ├── AutoLocaleManager  → Switch language at runtime
    ├── AutoLocaleProvider → Compose-native locale state
    └── AutoLocaleState    → Reactive locale holder
```

---

## 🛠 Installation

### 1. Add JitPack repository

In your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. Add the plugin

In your root `build.gradle.kts`:

```kotlin
plugins {
    id("io.github.autolocale") version "1.0.1" apply false
}
```

### 3. Add dependencies to your app module

In `app/build.gradle.kts`:

```kotlin
plugins {
    id("io.github.autolocale")
}

dependencies {
    implementation("com.github.ChFardeelAzhar:autolocale-android:1.0.1")
}
```

### 4. Configure AutoLocale

In `app/build.gradle.kts`:

```kotlin
autoLocale {
    languages = listOf("ar", "fr", "ur")   // Target languages
    enabled = true                          // Turn on/off
    dryRun = false                          // false = actually transform files
    groqApiKey = "your_groq_api_key"        // Free at console.groq.com
    excludeStrings = listOf("app_name")     // Strings to skip
}
```

> 🔑 **Get a free Groq API key at [console.groq.com](https://console.groq.com)** — no credit card required.
> Without a key, AutoLocale falls back to MyMemory (free, limited).

---

## 🚀 Usage

### Step 1 — Wrap your app with `AutoLocaleProvider`

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoLocaleProvider {        // ← Add this wrapper
                YourAppTheme {
                    AppNavigation()
                }
            }
        }
    }
}
```

### Step 2 — Write your UI normally (hardcoded strings are fine!)

```kotlin
@Composable
fun LoginScreen() {
    Column {
        Text("Welcome back")
        Text("Please enter your credentials")
        Button(onClick = { }) {
            Text("Login")
        }
        Text("Forgot your password?")
    }
}
```

### Step 3 — Run the Gradle task

```bash
./gradlew scanStrings
```

### Step 4 — That's it ✅

Your code is now localized:

```kotlin
// Auto-generated by AutoLocale
@Composable
fun LoginScreen() {
    Column {
        Text(stringResource(R.string.al_welcome_back))
        Text(stringResource(R.string.al_please_enter_your_credentials))
        Button(onClick = { }) {
            Text(stringResource(R.string.al_login))
        }
        Text(stringResource(R.string.al_forgot_your_password))
    }
}
```

---

## 🌐 Runtime Language Switching

Switch language at runtime — no Activity restart needed:

```kotlin
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    Row {
        Button(onClick = { AutoLocaleManager.setLanguage(context, "en") }) {
            Text("English")
        }
        Button(onClick = { AutoLocaleManager.setLanguage(context, "ar") }) {
            Text("العربية")
        }
        Button(onClick = { AutoLocaleManager.setLanguage(context, "fr") }) {
            Text("Français")
        }
    }
}
```

Arabic automatically switches to RTL layout. No extra configuration needed.

---

## 🔍 How It Works — The 5 Pillars

### 1. Scanner
At build time, AutoLocale scans all `.kt` files in your `src/main` directory. It detects hardcoded strings inside `Text()` composables using a context-aware regex engine — including named parameters (`text = "..."`), and multiline strings (`"""..."""`).

```
Text("Hello")               ✅ Detected
Text(text = "Hello")        ✅ Detected
Text("""
    Multiline
""")                        ✅ Detected
// Text("commented out")    ✅ Safely skipped
val x = "not in Text()"    ✅ Safely skipped
```

### 2. Key Generator
Converts each string into a deterministic, collision-safe XML key:

```
"Welcome to our app"    →  al_welcome_to_our_app
"Hello World"           →  al_hello_world
"Hello World!"          →  al_hello_world_2   ← collision handled
```

Same string always gets the same key — even across multiple files. Already-scanned strings are never processed twice.

### 3. XML Manager
Creates and updates `strings.xml` files for every configured language. Never overwrites existing translations — only adds missing keys. Automatically creates `values-ar/`, `values-fr/` directories if they don't exist.

```
Existing key → skipped (safe)
New key      → translated & added
```

### 4. AI Translation (Groq + MyMemory fallback)
Translations are powered by **Groq AI (Llama 3.1)** — fast, accurate, and free. If Groq fails or no key is provided, it automatically falls back to **MyMemory API**.

```
Groq API key provided → Groq AI (recommended)
No key provided       → MyMemory (zero setup, rate limited)
Either fails          → English fallback (build never breaks)
```

### 5. Source Transformer
Rewrites your `.kt` source files in-place — replacing raw string literals with `stringResource()` calls. Automatically injects required imports. Supports `dryRun = true` mode to preview changes before applying.

```kotlin
// Before
Text("Settings")

// After (auto-rewritten)
Text(stringResource(R.string.al_settings))
```

---

## ⚙️ Configuration Reference

```kotlin
autoLocale {
    // Required: languages to translate into
    languages = listOf("ar", "fr", "ur", "de", "es")

    // Optional: strings to skip (exact match)
    excludeStrings = listOf("app_name", "debug_only_string")

    // Optional: disable plugin entirely
    enabled = true

    // Optional: preview mode — shows what WOULD change, doesn't write files
    dryRun = false

    // Optional: Groq API key for AI-powered translation
    // Get free key at console.groq.com
    groqApiKey = "gsk_..."
}
```

---

## 🔄 Dry Run Mode

Not sure yet? Run in preview mode first:

```kotlin
autoLocale {
    dryRun = true   // ← just shows what would happen
    languages = listOf("ar", "fr")
}
```

```bash
./gradlew scanStrings

# Output:
# AutoLocale: [Dry Run] Would transform MainActivity.kt
# AutoLocale: Found 7 hardcoded string(s):
#   → [al_welcome] "Welcome" | MainActivity.kt:24
#   → [al_login]   "Login"   | MainActivity.kt:31
```

Set `dryRun = false` when you're ready to apply.

---

## 🔐 API Key Security

**Never hardcode your Groq API key in `build.gradle.kts`.** Use `local.properties` instead:

```properties
# local.properties (already in .gitignore)
groq.api.key=gsk_your_key_here
```

```kotlin
// build.gradle.kts
import java.util.Properties

val localProps = Properties()
localProps.load(rootProject.file("local.properties").inputStream())

autoLocale {
    languages = listOf("ar", "fr")
    groqApiKey = localProps["groq.api.key"] as String?
}
```

---

## 📋 Before vs After — Real Project Example

### Before AutoLocale

```
📁 res/
  └── values/
        strings.xml          ← manually maintained
  
No values-ar/, values-fr/ exists.
Developer manually copies & translates every string.
One missed string = broken Arabic build.
```

```kotlin
// MainActivity.kt
Text("Welcome to AutoLocale")
Text("Switch Language:")
Text("Click Me")
Button { Text("Arabic") }
Button { Text("French") }
```

### After AutoLocale — One Command

```
📁 res/
  ├── values/
  │     strings.xml          ← auto-updated
  ├── values-ar/
  │     strings.xml          ← auto-generated + translated
  ├── values-fr/
  │     strings.xml          ← auto-generated + translated
  └── values-ur/
        strings.xml          ← auto-generated + translated
```

```kotlin
// MainActivity.kt — auto-transformed
Text(stringResource(R.string.al_welcome_to_autolocale))
Text(stringResource(R.string.al_switch_language))
Text(stringResource(R.string.al_click_me))
Button { Text(stringResource(R.string.al_arabic)) }
Button { Text(stringResource(R.string.al_french)) }
```

---

## ❓ FAQ

<details>
<summary><strong>Will it touch my existing manually-written strings?</strong></summary>

No. AutoLocale only adds new keys. If a key already exists in `strings.xml`, it is skipped entirely. Your existing translations are always safe.
</details>

<details>
<summary><strong>What if the translation is wrong?</strong></summary>

AutoLocale is a starting point, not a final product. AI translations are good (~90% accurate) but should be reviewed by a native speaker before production. You can always manually correct a translation in the XML — AutoLocale will skip that key on future scans.
</details>

<details>
<summary><strong>Does it work with strings inside non-Compose code?</strong></summary>

Currently AutoLocale targets Jetpack Compose `Text()` composables. XML layout strings and non-Compose code are not scanned in this version.
</details>

<details>
<summary><strong>Does it work with string interpolation like `"Hello $name"`?</strong></summary>

AutoLocale detects these strings and adds them to `strings.xml`. However, for production use, strings with variables should be manually converted to Android format strings (`Hello %s`). This is on the roadmap for automatic handling.
</details>

<details>
<summary><strong>What happens if my internet is down during build?</strong></summary>

AutoLocale never fails your build. If translation fails for any reason (no internet, API error, rate limit), it falls back to the original English value and prints a warning. Your build always succeeds.
</details>

<details>
<summary><strong>Is my code sent to any server?</strong></summary>

Only the string values (e.g., `"Welcome to our app"`) are sent to the Groq API for translation — never your code, file names, or project structure.
</details>

---

## 🗺️ Roadmap

- [ ] String interpolation → auto-convert `$variable` to `%s` format strings
- [ ] Support for `stringResource()` in non-Text composables (`Button`, `ContentDescription`, etc.)
- [ ] Incremental scanning — only re-scan changed files
- [ ] Multiple AI provider support (Gemini, OpenAI)
- [ ] IntelliJ plugin for in-IDE preview
- [ ] Maven Central publishing

---

## 📄 License

```
MIT License

Copyright (c) 2025 Fardeel Azhar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software.
```

---

<div align="center">

## 🚀 Support & Contribution

If **AutoLocale** saves you hours of localization work, please consider supporting the project!

<p align="center">
  <a href="https://github.com/ChFardeelAzhar/autolocale-android">
    <img src="https://img.shields.io/badge/Star%20on%20GitHub-24292e?logo=github&logoColor=white&style=flat" />
  </a>
  &nbsp;&nbsp;
  <a href="https://fardeel.gumroad.com">
    <img src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-%245-orange?logo=buy-me-a-coffee&style=flat" />
  </a>
</p>

**Made with ❤️ for the Android Community**

*No more `strings.xml` hell. Ever again.*

</div>
