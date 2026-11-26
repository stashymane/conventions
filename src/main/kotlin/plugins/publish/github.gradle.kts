package plugins.publish

import org.gradle.api.credentials.PasswordCredentials

plugins {
    id("plugins.publish")
}

private fun ProviderFactory.githubUsername(): Provider<String> =
    gradleProperty("gpr.user")
        .orElse(environmentVariable("GITHUB_ACTOR"))

private fun ProviderFactory.githubPassword(): Provider<String> =
    gradleProperty("gpr.key")
        .orElse(environmentVariable("GITHUB_TOKEN"))

pluginManager.withPlugin("com.vanniktech.maven.publish") {
    extensions.configure<PublishingExtension>("publishing") {
        repositories {
            maven {
                name = "githubPackages"
                val repository = providers.gradleProperty("GITHUB_PACKAGES_REPOSITORY")
                url = uri("https://maven.pkg.github.com/${repository.get()}")
                credentials {
                    username = providers.githubUsername().get()
                    password = providers.githubPassword().get()
                }
                credentials(PasswordCredentials::class)
            }
        }
    }
}
