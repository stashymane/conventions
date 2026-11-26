@file:OptIn(ExperimentalWasmDsl::class)

package multiplatform.target.wasmJs

import multiplatform.target.util.TestBrowser
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val browserLocations = TestBrowser.getLocations(rootProject)

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

                browserLocations.forEach { (browser, location) ->
                    location.orNull?.let { environment(browser.envName, it) }
                }
            }
        }
    }
}
