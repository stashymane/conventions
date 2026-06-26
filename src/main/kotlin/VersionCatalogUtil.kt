import gradle.kotlin.dsl.accessors._e422e1462cee4e63d7e2347987997eb8.versionCatalogs
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint

internal fun Project.getVersionCatalog(name: String): VersionCatalog =
    this.versionCatalogs.getOrThrow(name)


internal fun VersionCatalogsExtension.getOrThrow(alias: String): VersionCatalog = find(alias)
    .orElseThrow { IllegalArgumentException("Version catalog named \"$alias\" is not present") }

internal fun VersionCatalog.getVersionOrThrow(alias: String): VersionConstraint =
    findVersion(alias).orElseThrow { IllegalArgumentException("\"$alias\" version was not found in catalog") }

internal fun VersionCatalog.getIntVersionOrThrow(alias: String): Int =
    getVersionOrThrow(alias).requiredVersion.toIntOrElseThrow {
        IllegalArgumentException("Failed to parse version \"$alias\" of \"$it\" as integer")
    }
