// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

fun resolveAdb(): String {
    // 1. local.properties (standard Android convention)
    val localProps = file("local.properties")
    if (localProps.exists()) {
        val props = java.util.Properties().apply { load(localProps.inputStream()) }
        props.getProperty("sdk.dir")?.let { sdkDir ->
            val adb = file("$sdkDir/platform-tools/adb")
            if (adb.exists()) return adb.absolutePath
        }
    }
    // 2. ANDROID_HOME env var
    System.getenv("ANDROID_HOME")?.let { home ->
        val adb = file("$home/platform-tools/adb")
        if (adb.exists()) return adb.absolutePath
    }
    // 3. Fall back to PATH
    return "adb"
}

tasks.register("run") {
    group = "quickstart"
    description = "Build, install, and launch the debug app on a connected device or emulator"
    dependsOn(":app:installDebug")

    doLast {
        val adb = resolveAdb()
        val result = ProcessBuilder(adb, "shell", "am", "start", "-n",
                "com.auth0.samples/.MainActivity")
            .inheritIO()
            .start()
            .waitFor()
        if (result != 0) error("adb shell am start failed with exit code $result")
        logger.lifecycle("App is running on device")
    }
}