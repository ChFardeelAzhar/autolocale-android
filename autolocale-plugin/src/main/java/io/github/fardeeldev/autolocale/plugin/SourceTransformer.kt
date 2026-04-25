package io.github.fardeeldev.autolocale.plugin

import java.io.File

object SourceTransformer {

    fun transformFile(file: File, foundStrings: List<HardcodedString>, dryRun: Boolean, namespace: String) {
        if (foundStrings.isEmpty()) return

        var content = file.readText()
        val originalContent = content
        
        // Replace each string literal with stringResource call (Context-aware)
        foundStrings.forEach { item ->
            val escapedLiteral = Regex.escape(item.rawLiteral)
            // Regex jo sirf Text() ke context mein match karta hai
            val pattern = """(Text\s*\(\s*(?:text\s*=\s*)?)$escapedLiteral""".toRegex()
            
            content = pattern.replace(content) { matchResult ->
                val prefix = matchResult.groupValues[1]
                
                // Check karo ke ye line comment to nahi hai
                val matchIndex = matchResult.range.first
                val lineStart = content.lastIndexOf('\n', matchIndex) + 1
                val line = content.substring(lineStart, matchIndex)
                
                if (line.trimStart().startsWith("//")) {
                    matchResult.value // Commented hai, original hi rehne do
                } else {
                    "${prefix}stringResource(R.string.${item.key})"
                }
            }
        }

        // Import management
        if (content != originalContent) {
            content = addImportIfMissing(content, "androidx.compose.ui.res.stringResource")
            
            // R import handling
            if (namespace.isNotEmpty()) {
                content = addImportIfMissing(content, "$namespace.R")
            }
        }

        if (dryRun) {
            println("AutoLocale: [Dry Run] Would transform ${file.name}")
        } else if (content != originalContent) {
            file.writeText(content)
            println("AutoLocale: Transformed ${file.name}")
        }
    }

    private fun addImportIfMissing(content: String, importPath: String): String {
        if (content.contains("import $importPath")) return content
        
        // Agar ye R import hai aur package name same hai, to import ki zaroorat nahi
        val packageName = content.lines().find { it.startsWith("package ") }?.removePrefix("package ")?.trim()
        if (importPath.endsWith(".R") && importPath.removeSuffix(".R") == packageName) {
            return content
        }
        
        val lines = content.lines().toMutableList()
        val packageIndex = lines.indexOfFirst { it.startsWith("package ") }
        
        if (packageIndex != -1) {
            // Add after package with an empty line
            lines.add(packageIndex + 1, "")
            lines.add(packageIndex + 2, "import $importPath")
            
            // Clean up multiple empty lines if any
            return lines.joinToString("\n").replace("\n\n\n", "\n\n")
        }
        
        return "import $importPath\n" + content
    }
}
