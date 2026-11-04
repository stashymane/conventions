package plugins.dokka

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class DokkaAssetsTask : DefaultTask() {
    @get:OutputFile
    abstract val stylesheet: RegularFileProperty

    @get:OutputFiles
    abstract val icons: ConfigurableFileCollection

    @TaskAction
    fun generate() {
        writeResource("dokka.css", stylesheet.get().asFile)

        icons.files.forEach { icon ->
            writeResource("icons/${icon.name.removePrefix("source-set-")}", icon)
        }
    }

    private fun writeResource(name: String, target: File) {
        val bytes = checkNotNull(javaClass.getResourceAsStream(name)) {
            "Missing bundled Dokka asset: $name"
        }.use { it.readBytes() }

        target.parentFile.mkdirs()
        target.writeBytes(bytes)
    }
}
