package multiplatform.plugin

import tasks.InjectPreloads

tasks.withType<Sync>().named {
    it.endsWith("browserDistribution", true)
            || it.endsWith("browserDevelopmentExecutableDistribution", true)
}.whenTaskAdded {
    val injectTask = tasks.register<InjectPreloads>("${name}InjectPreloads") {
        description = "Inject preload tags into ${this@whenTaskAdded.name}"
        distribution.set(layout.dir(provider { destinationDir }))
    }

    finalizedBy(injectTask)
}
