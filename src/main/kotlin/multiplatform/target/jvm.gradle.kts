package multiplatform.target

import getIntVersionOrThrow
import getVersionCatalog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("multiplatform.base")
}

configure<KotlinMultiplatformExtension> {
    val libs = getVersionCatalog("libs")
    val jvmVersion = libs.getIntVersionOrThrow("jvm").toString()
    val targetVersion = libs.findVersion("jvm-target").orElse(null)?.requiredVersion

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(targetVersion ?: jvmVersion)
        }
    }
}
