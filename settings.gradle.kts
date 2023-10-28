pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "DoraemonX"
include(":app")
include(":doraemon")


gradle.afterProject {
    val localProperties = readPropertiesIfExist(File(settingsDir, "local.properties"))
    extra.properties["IS_DEV"] =
        localProperties.getProperty("IS_DEV").toBooleanStrictOrNull() ?: false

}

fun readPropertiesIfExist(propertiesFile: File): java.util.Properties {
    val result = java.util.Properties()
    if (propertiesFile.exists()) {
        propertiesFile.bufferedReader().use {
            result.load(it)
        }
    }
    return result
}
