package plugins.preload

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

private val WEB_ARTIFACT_INCLUDES = listOf("*.wasm", "*.mjs", "*.js")

/** File tree of discoverable web artifacts under [directory]. */
internal fun Project.webArtifactTree(directory: Any): FileTree =
    objects.fileTree().from(directory).matching {
        include(WEB_ARTIFACT_INCLUDES)
    }

/** Applies shared [WebPreloadExtension] settings to an [InjectPreloads] task. */
internal fun InjectPreloads.configureFrom(
    extension: WebPreloadExtension,
    indexTemplate: Provider<RegularFile>,
    outputIndex: Provider<RegularFile>,
) {
    this.indexTemplate.set(extension.indexHtml.orElse(indexTemplate))
    this.outputIndex.set(outputIndex)

    discoverDistribution.set(extension.distribution.enabled)
    discoverWasm.set(extension.distribution.wasm)
    discoverMjs.set(extension.distribution.mjs)
    discoverJs.set(extension.distribution.js)

    preloadAssets.set(extension.assets)
    prefetchAssets.set(extension.prefetch.assets)
}

/** Registers [InjectPreloads] under [name], or returns the existing provider. */
internal fun Project.injectPreloads(
    name: String,
    description: String,
    extension: WebPreloadExtension,
    indexTemplate: Provider<RegularFile>,
    outputIndex: Provider<RegularFile>,
    configure: InjectPreloads.() -> Unit = {},
): TaskProvider<InjectPreloads> {
    if (tasks.names.contains(name)) {
        return tasks.named(name, InjectPreloads::class.java)
    }

    return tasks.register(name, InjectPreloads::class.java) {
        this.description = description
        configureFrom(extension, indexTemplate, outputIndex)
        configure()
    }
}

/** `index.html` from [compilation]'s source-set hierarchy (most specific first). */
internal fun Project.indexHtmlForCompilation(
    compilation: KotlinCompilation<*>,
): Provider<RegularFile> =
    layout.file(
        provider {
            compilation.defaultSourceSet.findIndexHtml()
                ?: error(
                    "No index.html in source sets of '${compilation.target.targetName}' " +
                        "'${compilation.name}' compilation. Set webPreload.indexHtml explicitly.",
                )
        },
    )

private fun KotlinSourceSet.findIndexHtml(): File? {
    resources.srcDirs.forEach { dir ->
        val index = File(dir, "index.html")
        if (index.isFile) return index
    }
    dependsOn.forEach { parent ->
        parent.findIndexHtml()?.let { return it }
    }
    return null
}

internal fun Sync.writesTo(directory: File): Boolean {
    val destination = runCatching { destinationDir }.getOrNull() ?: return false
    return destination.canonicalFile == directory.canonicalFile
}

internal fun Provider<Directory>.indexHtml(): Provider<RegularFile> =
    map { it.file("index.html") }
