package multiplatform.target

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import projectIdentifier

plugins {
    id("multiplatform.base")
}

configure<KotlinMultiplatformExtension> {
    js {
        outputModuleName.convention(provider(::projectIdentifier))

        browser()
        nodejs()
    }
}
