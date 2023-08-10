@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.java.qa)
}

subprojects {

    apply(plugin = "java-library")

    group = "it.unibo.ds.chainvote"

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("com.github.spotbugs:spotbugs-annotations:4.7.3")
        testCompileOnly("com.github.spotbugs:spotbugs-annotations:4.7.3")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
