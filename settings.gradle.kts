
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://jitpack.io")

        }
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.speedcheckerapi.com/artifactory/libs-release")
            credentials {
                username = "demo"
                password = "AP85qiz6wYEsCttWU2ZckEWSwJKuA6mSYcizEY"
            }
        }
    }

}

rootProject.name = "NetworkTestApp"
include(":app")
 