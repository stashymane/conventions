package plugins.preload

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

private const val PRELOAD_TOKEN: String = "<!-- {PRELOADS} -->"
private const val PREFETCH_TOKEN: String = "<!-- {PREFETCHES} -->"

abstract class InjectPreloads : DefaultTask() {
    /**
     * Source template with inject markers. Must not be the distribution copy —
     * that file is overwritten by this task and would break incremental rebuilds.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val indexTemplate: RegularFileProperty

    @get:OutputFile
    abstract val outputIndex: RegularFileProperty

    /**
     * Extra `index.html` destinations that should receive the same injected
     * content (e.g. webpack link-sync dist, which the dev server serves before
     * processed resources).
     */
    @get:OutputFiles
    @get:Optional
    abstract val additionalIndexes: ConfigurableFileCollection

    /**
     * Artifacts considered for auto-discovery (typically `*.wasm` / `*.mjs` /
     * `*.js`). Excludes `index.html` so writing the output does not invalidate
     * this task's own inputs.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:Optional
    abstract val discoverFrom: ConfigurableFileCollection

    @get:Input
    abstract val discoverDistribution: Property<Boolean>

    @get:Input
    abstract val discoverWasm: Property<Boolean>

    @get:Input
    abstract val discoverMjs: Property<Boolean>

    @get:Input
    abstract val discoverJs: Property<Boolean>

    @get:Nested
    abstract val preloadAssets: ListProperty<LinkAsset>

    @get:Nested
    abstract val prefetchAssets: ListProperty<LinkAsset>

    init {
        // Link-sync / processResources can restore a pristine index.html after a
        // previous inject; treat any output that still has markers as out of date.
        outputs.upToDateWhen {
            indexOutputs().all { file ->
                file.isFile && PRELOAD_TOKEN !in file.readText()
            }
        }
    }

    @TaskAction
    fun run() {
        val template = indexTemplate.get().asFile
        require(template.isFile) { "index template not found: ${template.absolutePath}" }

        var html = template.readText()

        val preloadLines = buildList {
            addDiscovered()
            preloadAssets.get().forEach { add(it.toHtml()) }
        }
        val prefetchLines = prefetchAssets.get().map { it.toHtml() }

        require(PRELOAD_TOKEN in html) {
            "Missing $PRELOAD_TOKEN marker in ${template.absolutePath}"
        }

        html = when {
            PREFETCH_TOKEN in html -> {
                html.replaceToken(PRELOAD_TOKEN, preloadLines)
                    .replaceToken(PREFETCH_TOKEN, prefetchLines)
            }

            prefetchLines.isEmpty() -> html.replaceToken(PRELOAD_TOKEN, preloadLines)

            else -> html.replaceToken(PRELOAD_TOKEN, preloadLines + prefetchLines)
        }

        indexOutputs().forEach { output ->
            output.parentFile?.mkdirs()
            output.writeText(html)
        }
    }

    private fun indexOutputs(): List<File> =
        buildList {
            add(outputIndex.get().asFile)
            addAll(additionalIndexes.files)
        }.distinctBy { it.canonicalFile }

    private fun MutableList<String>.addDiscovered() {
        if (!discoverDistribution.get()) return

        val discoverWasm = discoverWasm.getOrElse(false)
        val discoverMjs = discoverMjs.getOrElse(false)
        val discoverJs = discoverJs.getOrElse(false)

        discoverFrom.files.sortedBy(File::getName).forEach { file ->
            when (file.extension) {
                "wasm" -> if (discoverWasm) {
                    add("""<link rel="preload" href="${file.name}" as="fetch" crossorigin>""")
                }

                "mjs" -> if (discoverMjs) {
                    add("""<link rel="modulepreload" href="${file.name}">""")
                }

                "js" -> if (discoverJs) {
                    add("""<link rel="preload" href="${file.name}" as="script">""")
                }
            }
        }
    }
}

/**
 * Replaces [token] with [lines], prefixing each line with the whitespace that
 * preceded the token on its line.
 */
private fun String.replaceToken(token: String, lines: List<String>): String {
    val tokenIndex = indexOf(token)
    require(tokenIndex >= 0) { "Missing $token" }

    val lineStart = lastIndexOf('\n', tokenIndex - 1) + 1
    val indent = substring(lineStart, tokenIndex)
    require(indent.all { it == ' ' || it == '\t' }) {
        "Expected only indentation before $token, found: \"$indent\""
    }

    val replacement = lines.joinToString("\n") { indent + it }
    return replaceRange(lineStart, tokenIndex + token.length, replacement)
}

private fun LinkAsset.toHtml(): String = buildString {
    append("""<link rel="${rel.get()}" href="${href.get()}"""")
    asType.orNull?.let { append(""" as="$it"""") }
    type.orNull?.let { append(""" type="$it"""") }
    if (crossorigin.getOrElse(false)) append(" crossorigin")
    append(">")
}
