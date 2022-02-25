plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "club.tesseract"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.mattstudios.me/artifactory/public")
    maven("https://repo1.maven.org/maven2/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.reflections:reflections:0.9.12")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
}
tasks {
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        relocate("kotlin", "com.github.tropicalshadow.tesseractitems.dependencies.kotlin")
        relocate("org.reflections", "com.github.tropicalshadow.tesseractitems.dependencies.reflections")
        exclude("DebugProbesKt.bin")
        exclude("META-INF/**")
    }

    processResources {
        filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf("version" to project.version))
    }

}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}