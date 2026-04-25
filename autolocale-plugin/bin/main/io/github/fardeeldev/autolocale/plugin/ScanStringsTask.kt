package io.github.fardeeldev.autolocale.plugin


import org.gradle.api.DefaultTask
import java.io.File
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class ScanStringsTask : DefaultTask() {

    @Internal
    var extension: AutoLocaleExtension = AutoLocaleExtension()

    @TaskAction
    fun scan() {
        if (!extension.enabled) {
            println("AutoLocale: Plugin disabled, skipping scan.")
            return
        }

        println("AutoLocale: Starting scan...")
        println("AutoLocale: Target languages → ${extension.languages}")
        
        val keyGenerator = KeyGenerator() // Per-session instance
        
        // Reflection se namespace nikaalo (taake AGP par direct dependency na ho)
        val androidExtension = project.extensions.findByName("android")
        val namespace = try {
            androidExtension?.let {
                it.javaClass.getMethod("getNamespace").invoke(it) as? String
            }
        } catch (e: Exception) {
            null
        } ?: ""

        // Sirf is module ke 'src/main' folder ke andar .kt files dhundo
        val kotlinFiles = project.fileTree(project.file("src/main")) {
            it.include("**/*.kt")
            it.exclude("**/build/**")
        }

        val foundStrings = mutableListOf<HardcodedString>()

        kotlinFiles.forEach { file ->
            val content = file.readText()
            
            // Regex jo Text components aur unki strings literal ko dhoondta hai
            // Supports: Text("..."), Text(text = "..."), Text("""...""")
            val textPattern = """Text\s*\(\s*(?:text\s*=\s*)?((?:"{3}[\s\S]*?"{3})|(?:"[^"]*"))""".toRegex()

            val matches = textPattern.findAll(content)

            matches.forEach { match ->
                val rawLiteral = match.groupValues[1]
                
                // Quotes remove karo sirf display/key ke liye
                val isMultiline = rawLiteral.startsWith("\"\"\"")
                val value = if (isMultiline) {
                    rawLiteral.substring(3, rawLiteral.length - 3)
                } else {
                    rawLiteral.substring(1, rawLiteral.length - 1)
                }.trim()
                
                if (value.isNotEmpty() && value !in extension.excludeStrings) {
                    val offset = match.range.first
                    val lineNumber = content.substring(0, offset).count { it == '\n' } + 1
                    
                    val key = keyGenerator.generateKey(value)
                    
                    foundStrings.add(
                        HardcodedString(
                            key = key,
                            value = value,
                            rawLiteral = rawLiteral,
                            filePath = file.absolutePath,
                            lineNumber = lineNumber
                        )
                    )
                }
            }
        }

        // Phase 2: XML Update logic
        if (foundStrings.isNotEmpty()) {
            val resDir = project.file("src/main/res")
            if (!resDir.exists()) {
                println("AutoLocale: Warning - res directory not found at ${resDir.absolutePath}")
                return
            }

            println("AutoLocale: Updating XML files for languages: ${extension.languages}")
            
            foundStrings.forEach { item ->
                // Default language (English)
                XmlManager.updateStringsXml(resDir, null, item.key, item.value)
                
                // Target languages
                extension.languages.forEach { lang ->
                    XmlManager.updateStringsXml(resDir, lang, item.key, item.value)
                }
            }
        }

        // Phase 3: Source Transformation logic
        if (foundStrings.isNotEmpty()) {
            val filesToTransform = foundStrings.groupBy { it.filePath }
            filesToTransform.forEach { (path, strings) ->
                SourceTransformer.transformFile(File(path), strings, extension.dryRun, namespace)
            }
        }

        // Results print karo
        if (foundStrings.isEmpty()) {
            println("AutoLocale: No hardcoded strings found.")
        } else {
            println("AutoLocale: Found ${foundStrings.size} hardcoded string(s):")
            foundStrings.forEach {
                println("  → [${it.key}] \"${it.value}\" | ${it.filePath}:${it.lineNumber}")
            }
        }
    }
}

// Ek hardcoded string ka model
data class HardcodedString(
    val key: String,
    val value: String,
    val rawLiteral: String,
    val filePath: String,
    val lineNumber: Int
)