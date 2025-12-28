plugins {
    id("java")
    id("war")
}

allprojects {
    group = "ru.itmo.cs.dandadan"
    repositories {
        mavenCentral()
    }

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(11)
    }
}
