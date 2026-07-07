package multiplatform.target

import applyJvmVersions
import getVersionCatalog
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("multiplatform.base")
}

configure<KotlinMultiplatformExtension> {
    jvm("desktop") {
        applyJvmVersions(getVersionCatalog("libs"))
    }
}
