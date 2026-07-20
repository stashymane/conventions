package multiplatform.lib

import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("multiplatform.target.jvm")
    id("multiplatform.target.jvmDebug")
}

configure<KotlinMultiplatformExtension> {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable {
                mainClass.convention("io.ktor.server.cio.EngineMain")
            }
            executable("debug") {
                mainClass.convention("io.ktor.server.cio.EngineMain")
                applicationDefaultJvmArgs.add("-Dio.ktor.development=true")
                applicationDefaultJvmArgs.add("--enable-native-access=ALL-UNNAMED")
            }
        }
    }
}
