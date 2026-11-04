package plugins

import org.jetbrains.dokka.gradle.DokkaExtension
import plugins.dokka.ReadmeModuleDocsTask

val moduleDocs = tasks.register("dokkaReadmeModuleDocs", ReadmeModuleDocsTask::class.java) {
    description = "Transforms README.md files on each module for Dokka"
    readme.from(layout.projectDirectory.file("README.md"))
    moduleName.set(project.name)
    outputFile.set(layout.buildDirectory.file("dokka/module-documentation.md"))
}

extensions.configure<DokkaExtension> {
    dokkaSourceSets.configureEach {
        includes.from(moduleDocs.flatMap(ReadmeModuleDocsTask::outputFile))
    }
}
