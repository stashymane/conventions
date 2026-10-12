package multiplatform.target.js

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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
            }
        }
    }
}
