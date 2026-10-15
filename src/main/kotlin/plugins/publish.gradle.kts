package plugins

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar

val skipJavadocOnSnapshot = providers
    .gradleProperty("publish.skipJavadocForSnapshots")
    .map(String::toBoolean)
    .orElse(false)

pluginManager.withPlugin("com.vanniktech.maven.publish") {
    @Suppress("UnstableApiUsage")
    extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
        val isSnapshot = version.toString().endsWith("-SNAPSHOT", ignoreCase = true)
        if (skipJavadocOnSnapshot.get() && isSnapshot) {
            configureBasedOnAppliedPlugins(
                javadocJar = JavadocJar.None(),
                sourcesJar = SourcesJar.Sources(),
            )
        } else {
            configureBasedOnAppliedPlugins()
        }
    }
}
