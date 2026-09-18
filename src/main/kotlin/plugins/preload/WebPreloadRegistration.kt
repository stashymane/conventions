package plugins.preload

import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.ir.Executable
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import java.io.File

/**
 * Wires [InjectPreloads] for each JS/Wasm [Executable]:
 * - processed resources + link-sync dist (webpack run / dev server)
 * - binary distribution directory (browser distribution Sync)
 */
internal fun Project.configureWebPreload(extension: WebPreloadExtension) {
    val kotlin = extensions.getByType(KotlinMultiplatformExtension::class.java)

    kotlin.targets.withType(KotlinJsIrTarget::class.java).configureEach {
        val target = this
        binaries.withType(Executable::class.java).configureEach {
            registerInjects(extension, target, this)
        }
    }
}

private fun Project.registerInjects(
    extension: WebPreloadExtension,
    target: KotlinJsIrTarget,
    binary: Executable,
) {
    val resourcesInject = registerProcessResourcesInject(extension, target, binary.compilation, binary)
    registerDistributionInject(extension, target, binary, resourcesInject)
}

private fun Project.registerProcessResourcesInject(
    extension: WebPreloadExtension,
    target: KotlinJsIrTarget,
    compilation: KotlinJsIrCompilation,
    binary: Executable,
): TaskProvider<InjectPreloads> {
    val injectName = "${target.targetName}ProcessResourcesInjectPreloads"
    val created = !tasks.names.contains(injectName)
    val processResources = tasks.named(compilation.processResourcesTaskName)

    val injectTask = injectPreloads(
        name = injectName,
        description =
            "Inject preload/prefetch tags into ${target.targetName} processed resources " +
                "(webpack run / dev server)",
        extension = extension,
        indexTemplate = indexHtmlForCompilation(compilation),
        outputIndex = layout.dir(provider { compilation.output.resourcesDir }).indexHtml(),
    ) {
        dependsOn(processResources)
    }

    if (created) {
        processResources.configure { finalizedBy(injectTask) }
        tasks.withType(KotlinWebpack::class.java).configureEach {
            if (this.compilation.target.targetName == target.targetName &&
                this.compilation.name == compilation.name
            ) {
                dependsOn(injectTask)
            }
        }
    }

    runCatching { binary.linkSyncTask }.getOrNull()?.let { linkSync ->
        injectTask.configure {
            dependsOn(linkSync)
            discoverFrom.from(
                linkSync.map { sync -> webArtifactTree(sync.destinationDirectory) },
            )
            // Dev server serves link-sync dist before processed resources.
            additionalIndexes.from(
                linkSync.map { sync -> File(sync.destinationDirectory.get(), "index.html") },
            )
        }
        linkSync.configure { finalizedBy(injectTask) }
    }

    return injectTask
}

private fun Project.registerDistributionInject(
    extension: WebPreloadExtension,
    target: KotlinJsIrTarget,
    binary: Executable,
    resourcesInject: TaskProvider<InjectPreloads>,
) {
    val outputDir = binary.distribution.outputDirectory

    val injectTask = injectPreloads(
        name = "${target.targetName}${binary.name.replaceFirstChar(Char::uppercaseChar)}InjectPreloads",
        description =
            "Inject preload/prefetch tags into ${target.targetName} ${binary.name} distribution",
        extension = extension,
        indexTemplate = indexHtmlForCompilation(binary.compilation),
        outputIndex = outputDir.indexHtml(),
    ) {
        discoverFrom.from(outputDir.map { dir -> webArtifactTree(dir) })
    }

    afterEvaluate {
        val dist = outputDir.get().asFile
        tasks.withType(Sync::class.java).configureEach {
            if (writesTo(dist)) {
                dependsOn(resourcesInject)
                finalizedBy(injectTask)
            }
        }
    }
}
