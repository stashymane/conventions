package android

import applyFrom
import com.android.build.api.dsl.ApplicationExtension
import getAndroidSdkVersion
import getIntVersionOrThrow
import getVersionCatalog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

configure<ApplicationExtension> {
    val libs = getVersionCatalog("libs")
    val androidLibs = getVersionCatalog("androidLibs")

    namespace = project.group.toString()

    compileSdk {
        applyFrom(androidLibs.getAndroidSdkVersion("compileSdk"))
    }

    defaultConfig {
        minSdk = androidLibs.getIntVersionOrThrow("minSdk")
        targetSdk = androidLibs.getIntVersionOrThrow("targetSdk")
    }

    val jvmVersion = JavaVersion.toVersion(libs.getIntVersionOrThrow("jvm"))
    compileOptions {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    extensions.configure<KotlinAndroidExtension> {
        val libs = getVersionCatalog("libs")

        compilerOptions {
            val jvmVersion = libs.getIntVersionOrThrow("jvm").toString()
            val targetVersion = libs.findVersion("jvm-target").orElse(null)?.requiredVersion

            jvmTarget = JvmTarget.fromTarget(targetVersion ?: jvmVersion)
        }
    }
}
