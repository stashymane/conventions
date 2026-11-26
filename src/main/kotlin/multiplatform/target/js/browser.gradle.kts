package multiplatform.target.js

import multiplatform.target.util.TestBrowser
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val browserLocations = TestBrowser.getLocations(rootProject)

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

                browserLocations.forEach { (browser, location) ->
                    location.orNull?.let { environment(browser.envName, it) }
                }
            }
        }
    }
}
