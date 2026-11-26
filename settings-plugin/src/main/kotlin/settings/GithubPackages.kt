package settings

import org.gradle.api.Plugin
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import java.net.URI

private fun ProviderFactory.githubUsername(): Provider<String> =
    gradleProperty("gpr.user")
        .orElse(environmentVariable("GITHUB_ACTOR"))

private fun ProviderFactory.githubPassword(): Provider<String> =
    gradleProperty("gpr.key")
        .orElse(environmentVariable("GITHUB_TOKEN"))

@Suppress("UnstableApiUsage")
fun Settings.githubPackages(
    repository: String,
    username: Provider<String>? = providers.githubUsername(),
    password: Provider<String>? = providers.githubPassword(),
    block: MavenArtifactRepository.() -> Unit = {}
) {
    val user = username?.orNull ?: return
    val pass = password?.orNull ?: return

    dependencyResolutionManagement.repositories.maven {
        name = "githubPackages-$repository"
        url = URI("https://maven.pkg.github.com/$repository")
        credentials {
            this.username = user
            this.password = pass
        }

        block()
    }
}

class GithubPackagesPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) = Unit
}
