package io.github.fardeeldev.autolocale.plugin

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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

        // Agar language set hai to translate karo, warna original value use karo
        val rawValue = if (language != null) translateText(value, language) else value
        val finalValue = escapeXml(rawValue)

        // Add new string before </resources>
        val newStringTag = "    <string name=\"$key\">$finalValue</string>\n"
        val updatedContent = content.replace("</resources>", "$newStringTag</resources>")
        
        stringsXml.writeText(updatedContent)
        println("AutoLocale: Added '$key' to $valuesDirName/strings.xml")
    }

    /**
     * Translates text using MyMemory API.
     * Fallback to original text on failure.
     */
    private fun translateText(text: String, targetLang: String): String {
        var retryCount = 0
        val maxRetries = 1

        while (retryCount <= maxRetries) {
            try {
                val encodedText = URLEncoder.encode(text, "UTF-8")
                val urlString = "https://api.mymemory.translated.net/get?q=$encodedText&langpair=en|$targetLang"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == 429) { // Too Many Requests
                    println("AutoLocale: Rate limit hit for '$text', retrying after 2s...")
                    Thread.sleep(2000)
                    retryCount++
                    continue
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                
                // Simple JSON parsing (substring based)
                val translated = response.substringAfter("\"translatedText\":\"").substringBefore("\"")
                
                // Rate limiting delay (1 second as requested)
                Thread.sleep(1000)
                
                return if (translated.isNotEmpty() && translated != "null") {
                    translated
                } else {
                    text
                }
            } catch (e: Exception) {
                if (retryCount < maxRetries) {
                    println("AutoLocale: Translation attempt ${retryCount + 1} failed, retrying...")
                    Thread.sleep(2000)
                    retryCount++
                } else {
                    println("AutoLocale: Warning - Translation failed for '$text' ($targetLang). Using English fallback. Error: ${e.message}")
                    return text
                }
            }
        }
        return text
    }
    /**
     * Escapes XML special characters.
     */
    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}
