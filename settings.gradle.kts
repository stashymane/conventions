dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            @Suppress("UnstableApiUsage")
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }

        mavenCentral()
        gradlePluginPortal()
    }

    versionCatalogs {
        register("buildLibs") {
            from(files("gradle/build.versions.toml"))
        }
    }
}

rootProject.name = "conventions"
