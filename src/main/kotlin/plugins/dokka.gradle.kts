@file:OptIn(InternalDokkaGradlePluginApi::class)

package plugins

import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.dokka.gradle.internal.InternalDokkaGradlePluginApi
import plugins.dokka.DokkaAssetsTask
import plugins.dokka.ReadmeModuleDocsTask

private val sourceSetIcons = listOf("android", "js", "jvm", "wasm-js", "wasm-wasi", "web")

plugins {
    id("org.jetbrains.dokka")
}

val moduleDocs = tasks.register("dokkaReadmeModuleDocs", ReadmeModuleDocsTask::class.java) {
    readme.from(layout.projectDirectory.file("README.md"))
    moduleName.set(project.name)
    outputFile.set(layout.buildDirectory.file("dokka/module-documentation.md"))
}

val assetsDir = layout.buildDirectory.dir("dokka/assets")
val assets = tasks.register("dokkaAssets", DokkaAssetsTask::class.java) {
    stylesheet.set(assetsDir.map { it.file("dokka.css") })
    icons.from(sourceSetIcons.map { name -> assetsDir.map { it.file("images/source-set-$name.svg") } })
}

extensions.configure<DokkaExtension> {
    dokkaSourceSets.configureEach {
        includes.from(moduleDocs.flatMap(ReadmeModuleDocsTask::outputFile))
    }

    pluginsConfiguration.named<DokkaHtmlPluginParameters>("html") {
        customStyleSheets.from(assets.flatMap(DokkaAssetsTask::stylesheet))
        customAssets.from(assets.map(DokkaAssetsTask::icons))
    }
}
