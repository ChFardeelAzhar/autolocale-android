# AutoLocale 🚀

**Zero-Touch Localization for Jetpack Compose**

AutoLocale is a powerful Gradle plugin and runtime library designed to automate the localization workflow in Android. It scans your Compose code for hardcoded strings, moves them to `strings.xml`, generates unique keys, and transforms your source code automatically—all in one command.

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/Jetpack-Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## ✨ Features
- 🔍 **Auto-Scan**: Automatically detects hardcoded strings in `Text()` composables.
- 🗝️ **Key Generation**: Generates clean, standardized `al_snake_case` resource keys.
- 📝 **XML Sync**: Synchronizes keys across multiple `strings.xml` locales (`en`, `ar`, `fr`, etc.) automatically.
- 🔄 **Code Transformation**: Replaces hardcoded strings with `stringResource(R.string.key)` and manages imports.
- 🌐 **Runtime Switching**: Effortlessly switch app languages at runtime using the modern Android 13+ approach.
- 🛡️ **Dry Run Mode**: Preview all changes in the terminal before they are applied to your source code.

---

## 🚀 Quick Start

### 1. Apply the Plugin
Add the plugin to your app-level `build.gradle.kts`:

```kotlin
plugins {
    id("io.github.fardeeldev.autolocale") version "1.0.0"
}

autoLocale {
    enabled = true
    dryRun = false // Set to false to apply changes to .kt files
    languages = listOf("ar", "fr", "ur") // Target languages
    excludeStrings = listOf("Debug", "Test") // Strings to ignore
}
```

### 2. Add Runtime Dependency
```kotlin
dependencies {
    implementation("io.github.fardeeldev:autolocale-runtime:1.0.0")
}
```

### 3. Run the Automation
Open your terminal and run:
```bash
./gradlew scanStrings
```

---

## 🌐 Runtime Language Switching
Change your app's language dynamically without manual restarts:

```kotlin
val context = LocalContext.current

Button(onClick = { 
    AutoLocaleManager.setLanguage(context, "ar") // Switch to Arabic
}) {
    Text("Switch to Arabic")
}
```

---

## 📖 How it Works
1. **Scan**: The plugin reads your `.kt` files and finds strings inside `Text()` components.
2. **Keying**: It creates a unique key for each string (e.g., `"Welcome"` -> `al_welcome`).
3. **XML Update**: It checks if the key exists in your `strings.xml`. If not, it adds it to all configured locales.
4. **Transform**: It rewrites your source code:
   - `Text("Welcome")` ➡️ `Text(stringResource(R.string.al_welcome))`
5. **Runtime**: The runtime module uses `AppCompatDelegate` to apply the chosen locale globally.

---

## 🛠️ Requirements
- Jetpack Compose project
- `AppCompatActivity` for runtime switching support
- Android 8.0+ (Min SDK 26)

---

## 🤝 Contributing
Contributions are welcome! If you find a bug or have a feature request, please open an issue.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
