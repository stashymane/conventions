package multiplatform.target

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import getIntVersionOrThrow
import getOrThrow
import getVersionCatalog
import getVersionOrThrow
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import toIntOrElseThrow

plugins {
    id("multiplatform.base")
}

configure<KotlinMultiplatformExtension> {
    this.extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
        val androidLibs = getVersionCatalog("androidLibs")

        namespace = project.group.toString()
        compileSdk = androidLibs.getIntVersionOrThrow("compileSdk")
        minSdk = androidLibs.getIntVersionOrThrow("minSdk")

        withJava()

        compilerOptions {
            val jvmVersion = androidLibs.getVersionOrThrow("jvm").requiredVersion
            jvmTarget = JvmTarget.fromTarget(jvmVersion)
        }
    }
}
