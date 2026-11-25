@file:OptIn(ExperimentalWasmDsl::class)

package multiplatform.target.wasmJs

import multiplatform.target.util.localBrowserTestLocations
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val browserLocations = localBrowserTestLocations()

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
                
                browserLocations.get().forEach { (envVariable, location) ->
                    environment(envVariable, location)
                }
            }
        }
    }
}
