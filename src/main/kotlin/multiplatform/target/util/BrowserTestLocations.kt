package multiplatform.target.util

import localProperties
import org.gradle.api.Project
import org.gradle.api.provider.Provider

@JvmInline
value class TestBrowser(val name: String) {
    val propertyName: String get() = "test.browser.${name.lowercase()}"
    val envName: String get() = "${name.uppercase()}_BIN"

    fun location(project: Project): Provider<String> =
        project.localProperties.map { it.getProperty(propertyName) }
            .orElse(project.providers.environmentVariable(envName))

    companion object {
        val All = listOf(
            TestBrowser("chromium"),
            TestBrowser("chrome"),
            TestBrowser("firefox"),
        )

        fun getLocations(project: Project) = All.associateWith { browser ->
            browser.location(project)
        }
    }
}
