plugins {
    id("java-library")
    id("java-gradle-plugin")  // ← yeh zaroori hai Gradle plugin ke liye
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

group = "io.github.fardeeldev"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

gradlePlugin {
    plugins {
        create("autoLocalePlugin") {
            id = "io.github.fardeeldev.autolocale"
            implementationClass = "io.github.fardeeldev.autolocale.plugin.AutoLocalePlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "io.github.fardeeldev"
            artifactId = "autolocale-plugin"
            version = "1.0.0-SNAPSHOT"
        }
    }
}

dependencies {
    implementation(gradleApi())
}