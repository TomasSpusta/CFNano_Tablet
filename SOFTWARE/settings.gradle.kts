pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()


    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Add jitpack
    }
}

rootProject.name = "NanoTablet RFID"
include(":app")
 