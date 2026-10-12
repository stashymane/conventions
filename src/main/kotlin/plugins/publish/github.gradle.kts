package plugins.publish

import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.publish.PublishingExtension

plugins {
    id("plugins.publish")
}

pluginManager.withPlugin("com.vanniktech.maven.publish") {
    extensions.configure<PublishingExtension>("publishing") {
        repositories {
            maven {
                name = "githubPackages"
                val repository = providers.gradleProperty("GITHUB_PACKAGES_REPOSITORY")
                url = uri("https://maven.pkg.github.com/${repository.get()}")
                credentials(PasswordCredentials::class)
            }
        }
    }
}
