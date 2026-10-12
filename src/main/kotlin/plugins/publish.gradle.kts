package plugins

import com.vanniktech.maven.publish.MavenPublishBaseExtension

pluginManager.withPlugin("com.vanniktech.maven.publish") {
    extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
        @Suppress("UnstableApiUsage")
        configureBasedOnAppliedPlugins()
    }
}
