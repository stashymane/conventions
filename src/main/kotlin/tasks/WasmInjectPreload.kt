package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

private const val PRELOAD_TOKEN: String = "<!-- {PRELOADS} -->"

abstract class InjectPreloads : DefaultTask() {
    @get:InputDirectory
    abstract val distribution: DirectoryProperty

    @TaskAction
    fun run() {
        val dir = distribution.get().asFile
        val index = File(dir, "index.html")
        var html = index.readText()

        val preloadTags = buildString {
            dir.listFiles()?.forEach {
                when (it.extension) {
                    "wasm" -> appendLine("""<link rel="preload" href="${it.name}" as="fetch" crossorigin>""")
//                    "js" -> appendLine("""<link rel="preload" href="${it.name}" as="script">""")
                    "mjs" -> appendLine("""<link rel="modulepreload" href="${it.name}">""")
                }
            }
        }

        html = html.replace(PRELOAD_TOKEN, preloadTags)

        index.writeText(html)
    }
}
