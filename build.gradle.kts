plugins {
    id("org.jetbrains.intellij") version "1.17.1" // Плагин для разработки плагинов IntelliJ IDEA
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
    version.set("2024.2.1") // Версия IntelliJ IDEA
    type.set("IU") // Community Edition или Ultimate Edition
    //plugins.set(listOf("org.jetbrains.kotlin"))
    //jbrVersion.set("17") // Или укажите нужную версию JBR, например, "17" для Java 17
}


tasks {
    patchPluginXml {
        sinceBuild.set("242")
        untilBuild.set("242.*")
    }
}