import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

fun KotlinJvmTarget.applyJvmVersions(libs: VersionCatalog) {
    val jvmVersion = libs.getIntVersionOrThrow("jvm").toString()
    val targetVersion = libs.findVersion("jvm-target").orElse(null)?.requiredVersion

    compilerOptions {
        jvmTarget.convention(JvmTarget.fromTarget(targetVersion ?: jvmVersion))
    }
}
