package io.github.fardeeldev.autolocale.plugin

import java.io.File

object XmlManager {

    /**
     * Updates or creates a strings.xml file with the given key-value pair.
     */
    fun updateStringsXml(resDir: File, language: String?, key: String, value: String) {
        val valuesDirName = if (language == null) "values" else "values-$language"
        val valuesDir = File(resDir, valuesDirName)
        if (!valuesDir.exists()) {
            valuesDir.mkdirs()
        }

        val stringsXml = File(valuesDir, "strings.xml")
        if (!stringsXml.exists()) {
            stringsXml.writeText("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n</resources>")
        }

        val content = stringsXml.readText()
        
        // Check if key already exists
        if (content.contains("name=\"$key\"")) {
            println("AutoLocale: Key '$key' already exists in $valuesDirName/strings.xml, skipping.")
            return
        }

        // Add new string before </resources>
        val newStringTag = "    <string name=\"$key\">$value</string>\n"
        val updatedContent = content.replace("</resources>", "$newStringTag</resources>")
        
        stringsXml.writeText(updatedContent)
        println("AutoLocale: Added '$key' to $valuesDirName/strings.xml")
    }
}
