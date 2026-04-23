package io.github.fardeeldev.autolocale.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoLocalePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // Step 1: Extension register karo
        // (developer apna config yahan dega, jaise languages list)
        val extension = project.extensions.create(
            "autoLocale",
            AutoLocaleExtension::class.java
        )

        // Step 2: Task register karo
        project.tasks.register("scanStrings", ScanStringsTask::class.java) {
            it.extension = extension
        }

        // Step 3: Build ke saath hook karo
        project.afterEvaluate {
            project.tasks.findByName("preBuild")
                ?.dependsOn("scanStrings")
        }
    }
}