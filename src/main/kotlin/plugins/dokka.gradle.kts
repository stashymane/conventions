@file:OptIn(InternalDokkaGradlePluginApi::class)

package plugins

import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.dokka.gradle.internal.InternalDokkaGradlePluginApi
import plugins.dokka.DokkaStylesheetTask
import plugins.dokka.ReadmeModuleDocsTask

plugins {
    id("org.jetbrains.dokka")
}

val moduleDocs = tasks.register("dokkaReadmeModuleDocs", ReadmeModuleDocsTask::class.java) {
    readme.from(layout.projectDirectory.file("README.md"))
    moduleName.set(project.name)
    outputFile.set(layout.buildDirectory.file("dokka/module-documentation.md"))
}

val stylesheet = tasks.register("dokkaStylesheet", DokkaStylesheetTask::class.java) {
    outputFile.set(layout.buildDirectory.file("dokka/dokka.css"))
}

extensions.configure<DokkaExtension> {
    dokkaSourceSets.configureEach {
        includes.from(moduleDocs.flatMap(ReadmeModuleDocsTask::outputFile))
    }

    pluginsConfiguration.named<DokkaHtmlPluginParameters>("html") {
        customStyleSheets.from(stylesheet.flatMap(DokkaStylesheetTask::outputFile))
    }
}
