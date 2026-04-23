package io.github.fardeeldev.autolocale.plugin

import java.io.File

object SourceTransformer {

    fun transformFile(file: File, foundStrings: List<HardcodedString>, dryRun: Boolean) {
        if (foundStrings.isEmpty()) return

        var content = file.readText()
        val originalContent = content
        
        // Replace each string literal with stringResource call
        foundStrings.forEach { item ->
            val target = item.rawLiteral
            val replacement = "stringResource(R.string.${item.key})"
            
            if (content.contains(target)) {
                content = content.replace(target, replacement)
            }
        }

        // Import management
        if (content != originalContent) {
            content = addImportIfMissing(content, "androidx.compose.ui.res.stringResource")
            
            // R import handling
            // If the file uses R.string.key but R is not imported and package is different, we might need it.
            // But for now, we assume R is in the same package or already accessible.
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
