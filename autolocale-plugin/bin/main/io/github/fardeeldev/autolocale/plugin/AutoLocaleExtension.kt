package io.github.fardeeldev.autolocale.plugin


open class AutoLocaleExtension {

    // Developer in languages me translate karna chahta hai
    // Usage: autoLocale { languages = listOf("ar", "fr", "ur") }
    var languages: List<String> = emptyList()

    // Konsi strings skip karni hain scan se
    // Usage: autoLocale { excludeStrings = listOf("app_name") }
    var excludeStrings: List<String> = emptyList()

    // Plugin on/off switch
    var enabled: Boolean = true

    // Dry run mode (sirf dikhayega, files modify nahi karega)
    var dryRun: Boolean = true

    // Groq AI API Key for better translations
    var groqApiKey: String? = null
}