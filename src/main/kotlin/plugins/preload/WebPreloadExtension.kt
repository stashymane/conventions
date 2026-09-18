package plugins.preload

import org.gradle.api.Action
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Nested
import javax.inject.Inject

abstract class WebPreloadExtension @Inject constructor(
    private val objects: ObjectFactory,
) {
    /**
     * Optional override for the pristine `index.html` template.
     *
     * When unset, each inject task resolves `index.html` from that executable's
     * compilation source-set hierarchy (default source set first, then
     * `dependsOn` parents — e.g. `wasmJsMain`, then `webMain`).
     */
    abstract val indexHtml: RegularFileProperty

    @get:Nested
    abstract val distribution: DiscoveryConfig

    @get:Nested
    abstract val assets: ListProperty<LinkAsset>

    val prefetch: PrefetchAssets = objects.newInstance(PrefetchAssets::class.java)

    fun distribution(configure: Action<in DiscoveryConfig>) = configure.execute(distribution)

    fun prefetch(configure: Action<in PrefetchAssets>) = configure.execute(prefetch)

    fun asset(href: String, configure: Action<in LinkAsset> = Action {}) {
        assets.add(newAsset(href, rel = "preload", configure))
    }

    fun module(href: String, configure: Action<in LinkAsset> = Action {}) {
        assets.add(
            newAsset(href, rel = "modulepreload") {
                configure.execute(this)
            },
        )
    }

    fun script(href: String, configure: Action<in LinkAsset> = Action {}) {
        asset(href) {
            asType.convention("script")
            configure.execute(this)
        }
    }

    fun style(href: String, configure: Action<in LinkAsset> = Action {}) {
        asset(href) {
            asType.convention("style")
            configure.execute(this)
        }
    }

    fun image(href: String, configure: Action<in LinkAsset> = Action {}) {
        asset(href) {
            asType.convention("image")
            configure.execute(this)
        }
    }

    /** CSS `@font-face` preload (`as="font"`). */
    fun font(href: String, configure: Action<in LinkAsset> = Action {}) {
        asset(href) {
            asType.convention("font")
            crossorigin.convention(true)
            configure.execute(this)
        }
    }

    /** Binary / Compose resource fetch preload (`as="fetch"`). */
    fun fetch(href: String, configure: Action<in LinkAsset> = Action {}) {
        asset(href) {
            asType.convention("fetch")
            crossorigin.convention(true)
            configure.execute(this)
        }
    }

    private fun newAsset(
        href: String,
        rel: String,
        configure: Action<in LinkAsset>,
    ): LinkAsset =
        objects.newInstance(LinkAsset::class.java).apply {
            this.href.set(href)
            this.rel.set(rel)
            crossorigin.convention(false)
            configure.execute(this)
        }
}
