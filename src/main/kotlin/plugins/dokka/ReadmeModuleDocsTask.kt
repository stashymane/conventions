package plugins.dokka

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

private val firstLevelHeading = Regex("(?m)^#\\s+.*$")

abstract class ReadmeModuleDocsTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val readme: ConfigurableFileCollection

    @get:Input
    abstract val moduleName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val output = outputFile.get().asFile
        output.parentFile.mkdirs()

        val readmeFile = readme.files.firstOrNull()?.takeIf(File::isFile)
        if (readmeFile == null) {
            output.writeText("")
            return
        }

        val header = "# Module ${moduleName.get()}"
        val readmeText = readmeFile.readText()

        val docs = if (firstLevelHeading.containsMatchIn(readmeText)) {
            readmeText.replaceFirst(firstLevelHeading, header)
        } else {
            "$header\n\n$readmeText"
        }

        output.writeText(docs)
    }
}
