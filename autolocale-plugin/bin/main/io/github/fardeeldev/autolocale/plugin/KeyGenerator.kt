package io.github.fardeeldev.autolocale.plugin

class KeyGenerator {
    
    private val valueToKey = mutableMapOf<String, String>()
    private val generatedKeys = mutableSetOf<String>()

    /**
     * Converts a raw string into a valid XML resource key.
     * Example: "Hello World!" -> "al_hello_world"
     * Handles collisions by adding a numeric suffix.
     */
    fun generateKey(value: String): String {
        // Agar ye string pehle se scan ho chuki hai, to wahi key wapis karo
        if (valueToKey.containsKey(value)) {
            return valueToKey[value]!!
        }

        val sanitized = value.lowercase()
            .replace(Regex("[^a-z0-9]"), "_") // Keep only alphanumeric
            .replace(Regex("_+"), "_")        // Remove duplicate underscores
            .trim('_')
        
        // Agar string empty hai (e.g. sirf symbols), to default key use karo
        val baseName = if (sanitized.isEmpty()) "string" else sanitized
        
        // Truncate if too long (max 50 chars)
        val truncated = if (baseName.length > 50) baseName.substring(0, 50).trim('_') else baseName
        
        val baseKey = "al_$truncated"
        var finalKey = baseKey
        var counter = 2
        
        // Collision handling: Agar key already used hai kisi aur string ke liye
        while (generatedKeys.contains(finalKey)) {
            finalKey = "${baseKey}_$counter"
            counter++
        }
        
        // Store for consistency and collision detection
        generatedKeys.add(finalKey)
        valueToKey[value] = finalKey
        
        return finalKey
    }
}
