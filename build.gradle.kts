plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.0.1"
}

group = "com.hotkeyhelper"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

dependencies {
    // JNA for Windows API access
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    mainModule.set("com.hotkeyhelper")
    mainClass.set("com.hotkeyhelper.App")
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "HotkeyHelper"
    }
    jpackage {
        imageName = "HotkeyHelper"
        skipInstaller = true
        imageOptions = listOf(
            "--app-version", project.version.toString(),
            "--description", "HotKey Helper - горячие клавиши для активного окна",
            "--vendor", "HotKey Helper"
        )
    }
}

// Copy data/ folder into jpackage output after build
tasks.named("jpackage") {
    doLast {
        copy {
            from("data")
            into(layout.buildDirectory.dir("jpackage/HotkeyHelper/data"))
        }
    }
}
