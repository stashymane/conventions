import com.android.build.api.dsl.CompileSdkSpec
import org.gradle.api.artifacts.VersionCatalog

internal data class AndroidSdkVersion(
    val major: Int,
    val minor: Int = 0,
) {
    init {
        require(major >= 1) { "Android SDK major API level must be >= 1, got $major" }
        require(minor >= 0) { "Android SDK minor API level must be >= 0, got $minor" }
    }
}

internal fun String.parseAndroidSdkVersion(): AndroidSdkVersion {
    val parts = split('.')
    if (parts.size > 2) {
        throw IllegalArgumentException(
            "Invalid Android SDK version \"$this\": expected format \"<major>\" or \"<major>.<minor>\"",
        )
    }

    val major = parts[0].toIntOrThrow {
        IllegalArgumentException("Failed to parse Android SDK major version \"${parts[0]}\" in \"$this\"")
    }
    val minor = if (parts.size == 2) {
        parts[1].toIntOrThrow {
            IllegalArgumentException("Failed to parse Android SDK minor version \"${parts[1]}\" in \"$this\"")
        }
    } else {
        0
    }

    return AndroidSdkVersion(major = major, minor = minor)
}

internal fun VersionCatalog.getAndroidSdkVersion(alias: String): AndroidSdkVersion =
    getVersionOrThrow(alias).requiredVersion.parseAndroidSdkVersion()

internal fun CompileSdkSpec.applyFrom(version: AndroidSdkVersion) {
    this.version = release(version.major) {
        if (version.minor != 0)
            minorApiLevel = version.minor
    }
}
