fun main() {
    val content = """
        Text(text = "Hello $name!")
        Text("Welcome to AutoLocale")
        Text(
            text = """
                This is a multiline
                hardcoded string.
            """.trimIndent()
        )
        Text("Click Me")
    """.trimIndent()
    
    val textPattern = "Text\\s*\\(\\s*(?:text\\s*=\\s*)?((?:\"[^\"]*\")|(?:\"\"\"[\\s\\S]*?\"\"\"))".toRegex()
    val matches = textPattern.findAll(content)
    
    println("Found ${matches.count()} matches")
    matches.forEach { match ->
        println("Match: ${match.groupValues[1]}")
    }
}
main()
