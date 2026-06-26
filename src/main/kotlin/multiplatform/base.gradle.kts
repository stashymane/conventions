package multiplatform

import getIntVersionOrThrow
import getVersionCatalog
import moduleName
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

configure<BasePluginExtension> {
    archivesName.convention(provider(project::moduleName))
}

configure<KotlinMultiplatformExtension> {
    val jvmVersion = getVersionCatalog("libs").getIntVersionOrThrow("jvm")

    jvmToolchain(jvmVersion)

    compilerOptions {
        optIn.addAll("kotlin.time.ExperimentalTime", "kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.addAll(
            "-Xreturn-value-checker=check",
            "-Xcontext-sensitive-resolution",
        )
    }
}
