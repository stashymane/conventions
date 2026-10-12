@file:OptIn(ExperimentalWasmDsl::class)

package multiplatform.target.wasmJs

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("multiplatform.target.wasmJs.common")
}

configure<KotlinMultiplatformExtension> {
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromiumHeadless()
                }
            }
        }
    }
}
