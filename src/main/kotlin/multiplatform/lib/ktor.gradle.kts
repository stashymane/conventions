package multiplatform.lib

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

plugins {
    id("multiplatform.target.jvm")
    id("multiplatform.target.jvmDebug")
}

val ktorConventions = extensions.create<KtorExtension>("ktorConventions").apply {
    main.mainClass.convention("io.ktor.server.cio.EngineMain")
    debug.mainClass.convention("io.ktor.server.cio.EngineMain")
}

configure<KotlinMultiplatformExtension> {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable(KotlinCompilation.MAIN_COMPILATION_NAME) {
                mainClass.set(ktorConventions.main.mainClass)
            }
            executable("debug") {
                mainClass.set(ktorConventions.debug.mainClass)
                applicationDefaultJvmArgs.add("-Dio.ktor.development=true")
                applicationDefaultJvmArgs.add("--enable-native-access=ALL-UNNAMED")
            }
        }
    }
}
