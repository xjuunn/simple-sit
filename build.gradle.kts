import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("fabric-loom") version "1.15-SNAPSHOT"
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.3.10"
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

base {
    archivesName = providers.gradleProperty("archives_base_name").get()
}

repositories {
}

dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")
}

tasks.processResources {
    inputs.property("version", version)
    inputs.property("minecraft_version", providers.gradleProperty("minecraft_version").get())
    inputs.property("loader_version", providers.gradleProperty("loader_version").get())
    inputs.property("fabric_api_version", providers.gradleProperty("fabric_api_version").get())

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "minecraft_version" to providers.gradleProperty("minecraft_version").get(),
            "loader_version" to providers.gradleProperty("loader_version").get(),
            "fabric_api_version" to providers.gradleProperty("fabric_api_version").get()
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val releaseFileBase = providers.provider {
    "${base.archivesName.get()}-${project.version}-fabricmc${providers.gradleProperty("minecraft_version").get()}"
}

tasks.named<Jar>("jar") {
    archiveBaseName.set(releaseFileBase)
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set(releaseFileBase)
    archiveClassifier.set("sources")
}

tasks.named<AbstractArchiveTask>("remapSourcesJar") {
    archiveBaseName.set(releaseFileBase)
    archiveClassifier.set("sources")
}

tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set(releaseFileBase)
    archiveClassifier.set("")
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {
    }
}
