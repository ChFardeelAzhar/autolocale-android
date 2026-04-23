package io.github.fardeeldev.autolocale.plugin

object KeyGenerator {
    
    /**
     * Converts a raw string into a valid XML resource key.
     * Example: "Hello World!" -> "al_hello_world"
     */
    fun generateKey(value: String): String {
        val sanitized = value.lowercase()
            .replace(Regex("[^a-z0-9]"), "_") // Keep only alphanumeric
            .replace(Regex("_+"), "_")        // Remove duplicate underscores
            .trim('_')
        
        // Truncate if too long (max 50 chars)
        val truncated = if (sanitized.length > 50) sanitized.substring(0, 50).trim('_') else sanitized
        
        return "al_$truncated"
    }
}
