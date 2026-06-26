package multiplatform.target

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import getIntVersionOrThrow
import getVersionCatalog
import getVersionOrThrow
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("multiplatform.base")
}

configure<KotlinMultiplatformExtension> {
    this.extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
        val libs = getVersionCatalog("libs")
        val androidLibs = getVersionCatalog("androidLibs")

        namespace = project.group.toString()
        compileSdk = androidLibs.getIntVersionOrThrow("compileSdk")
        minSdk = androidLibs.getIntVersionOrThrow("minSdk")

        withJava()

        compilerOptions {
            val jvmVersion = libs.getVersionOrThrow("jvm").requiredVersion
            jvmTarget = JvmTarget.fromTarget(jvmVersion)
        }
    }
}
