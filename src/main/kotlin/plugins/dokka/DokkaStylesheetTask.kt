package plugins.dokka

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class DokkaStylesheetTask : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val stylesheet = checkNotNull(javaClass.getResourceAsStream("dokka.css")) {
            "Missing bundled Dokka stylesheet"
        }

        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeBytes(stylesheet.use { it.readBytes() })
        }
    }
}
