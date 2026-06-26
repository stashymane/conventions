@file:OptIn(ExperimentalWasmDsl::class)

package multiplatform.target

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import projectIdentifier

plugins {
    id("multiplatform.base")
}

configure<KotlinMultiplatformExtension> {
    wasmJs {
        outputModuleName.convention(provider(::projectIdentifier))

        browser()
        nodejs()

        compilerOptions {
            freeCompilerArgs.addAll("-Xwasm-debugger-custom-formatters")
        }
    }
}
