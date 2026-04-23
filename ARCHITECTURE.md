# AutoLocale Architecture & Implementation Detail

This document explains the technical design decisions and implementation flow of the AutoLocale library for Senior Developers and Architects.

## 🌟 The Core Problem
In Jetpack Compose, hardcoding strings like `Text("Hello")` is a common developer shortcut that leads to technical debt. Manually moving these to `strings.xml` and creating keys for multiple languages is tedious and error-prone.

## 🏗️ Architecture Overview
AutoLocale is split into two distinct parts:
1. **The Gradle Plugin (`autolocale-plugin`)**: A build-time tool that handles static analysis and code transformation.
2. **The Runtime Library (`autolocale-runtime`)**: A lightweight helper to manage locale changes within the app.

---

## 🛠️ Implementation Workflow (The 5 Pillars)

### 1. Static Analysis (The Scanner)
Instead of a heavy AST (Abstract Syntax Tree) parser, we use a **High-Performance Regex Engine** optimized for Kotlin/Compose syntax.
- **Target**: `Text()` composables with single or triple-quoted string literals.
- **Pattern Recognition**: Handles named parameters (e.g., `text = "..."`) and positional parameters.
- **Multiline Support**: Correctly captures `"""..."""` blocks including indentation.

### 2. Key Generation Logic
We implement a **Deterministic Key Generator**:
- Converts strings to `al_` prefixed snake_case.
- Sanitizes special characters and limits key length.
- Ensures consistency: The same string always generates the same key across different files.

### 3. XML Synchronization (Resource Manager)
A custom DOM-based XML manager updates `strings.xml` files.
- **Safety**: It reads existing keys first to avoid duplicate entries or overwriting manual translations.
- **Multi-Locale**: Simultaneously updates `values/strings.xml`, `values-ar/`, `values-fr/`, etc.
- **Auto-Provisioning**: Automatically creates missing resource directories and files based on the Gradle configuration.

### 4. Source Transformation (The Rewrite Engine)
This is the most critical phase. The plugin performs a "Safe Rewrite":
- **Replacement**: Replaces the captured raw string literal with a `stringResource(R.string.key)` call.
- **Import Injection**: Detects if `androidx.compose.ui.res.stringResource` is missing and injects it at the top of the file without breaking package declarations.
- **Dry Run Support**: Allows developers to audit changes in the terminal before committing to disk.

### 5. Runtime Locale Management
Leverages the **Android 13 (API 33) Per-App Language** feature via `AppCompatDelegate`.
- **Backward Compatibility**: Works on older Android versions through the AppCompat bridge.
- **Zero-Restart Policy**: Triggers configuration changes that Compose's `stringResource` automatically reacts to, providing a seamless UX.

---

## 📈 Technical Choices & Rationale
- **Why Regex over KSP/Lint?**: KSP is great for code generation but doesn't easily support *modifying* existing source code. Lint is for reporting. Regex allows us to perform "Source-to-Source" transformation efficiently.
- **Why AppCompatActivity?**: While Compose can work with `ComponentActivity`, `AppCompatDelegate` provides a more robust bridge for runtime locale switching across all Android versions.
- **Namespace Handling**: The plugin automatically detects the project's namespace to reference the correct `R` file.

## 🚀 Future Roadmap
- **AST Parsing**: Transitioning to PSI/K2 for even deeper code understanding.
- **Interpolation Detection**: Auto-detecting `$var` and converting them to `%s` in XML.
- **AI Translation**: Integrating with LLMs to automatically translate the generated placeholder strings.
