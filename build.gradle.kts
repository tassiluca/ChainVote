@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.java.qa)
}

subprojects {

    apply(plugin = "java-library")
    apply(plugin = rootProject.libs.plugins.java.qa.get().pluginId)

    group = "it.unibo.ds.chainvote"

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(rootProject.libs.spotbugs.annotations)
        testCompileOnly(rootProject.libs.spotbugs.annotations)
        testImplementation(rootProject.libs.junit.api)
        testImplementation(rootProject.libs.junit.engine)
    }

    tasks.test {
        useJUnitPlatform()
    }
}
