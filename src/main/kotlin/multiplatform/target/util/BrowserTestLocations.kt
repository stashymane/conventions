package multiplatform.target.util

import localProperties
import org.gradle.api.Project
import org.gradle.api.provider.Provider

private val browserTestEnvVariables = mapOf(
    "chromium" to "CHROMIUM_BIN",
    "chrome" to "CHROME_BIN",
    "firefox" to "FIREFOX_BIN",
)

internal fun Project.localBrowserTestLocations(): Provider<Map<String, String>> =
    localProperties.map { props ->
        browserTestEnvVariables.mapNotNull { (browser, envVariable) ->
            props.getProperty("test.browser.$browser")?.let { envVariable to it }
        }.toMap()
    }
