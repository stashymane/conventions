package multiplatform.plugin

import tasks.InjectPreloads

private val taskNames = listOf(
    "browserDistribution",
    "browserProductionRun",
    "browserDevelopmentExecutableDistribution",
    "browserDevelopmentRun"
)

tasks.withType<Sync>().named { taskName ->
    taskNames.any { taskName.endsWith(it, true) }
}.whenTaskAdded {
    val injectTask = tasks.register<InjectPreloads>("${name}InjectPreloads") {
        description = "Inject preload tags into ${this@whenTaskAdded.name}"
        distribution.set(layout.dir(provider { destinationDir }))
    }

    finalizedBy(injectTask)
}
