plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("lib/jaylib-5.0.0-0.jar")) // Use files() to include the local JAR
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

application {
    mainClass.set("game.Main")
}
