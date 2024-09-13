plugins {
    id("org.jetbrains.intellij") version "1.13.0" // Плагин для разработки плагинов IntelliJ IDEA
    java
}

group = "com.example.chatplugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral() // Централизованный репозиторий Maven
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Для работы с API
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Для обработки JSON
}

intellij {
    version.set("2022.1.3") // Версия IntelliJ IDEA
    type.set("IU") // Community Edition или Ultimate Edition
    plugins.set(listOf("org.jetbrains.kotlin"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("223.*")
    }
}