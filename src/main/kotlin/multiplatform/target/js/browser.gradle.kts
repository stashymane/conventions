package multiplatform.target.js

import multiplatform.target.util.localBrowserTestLocations
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val browserLocations = localBrowserTestLocations()

plugins {
    id("multiplatform.target.js.common")
}

configure<KotlinMultiplatformExtension> {
    js {
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
