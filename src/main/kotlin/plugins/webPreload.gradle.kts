package plugins

import plugins.preload.WebPreloadExtension
import plugins.preload.configureWebPreload

val webPreload = extensions.create<WebPreloadExtension>("webPreload").apply {
    distribution.enabled.convention(true)
    distribution.wasm.convention(false)
    distribution.mjs.convention(false)
    distribution.js.convention(false)
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    configureWebPreload(webPreload)
}
