package plugins.preload

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Nested
import javax.inject.Inject

abstract class PrefetchAssets @Inject constructor(
    private val objects: ObjectFactory,
) {
    @get:Nested
    abstract val assets: ListProperty<LinkAsset>

    fun asset(href: String, configure: Action<in LinkAsset> = Action {}) {
        assets.add(newAsset(href, configure))
    }

    fun script(href: String, configure: Action<in LinkAsset> = Action {}) = asset(href) {
        asType.convention("script")
        configure.execute(this)
    }

    fun style(href: String, configure: Action<in LinkAsset> = Action {}) = asset(href) {
        asType.convention("style")
        configure.execute(this)
    }


    fun image(href: String, configure: Action<in LinkAsset> = Action {}) = asset(href) {
        asType.convention("image")
        configure.execute(this)
    }

    fun font(href: String, configure: Action<in LinkAsset> = Action {}) = asset(href) {
        asType.convention("font")
        crossorigin.convention(true)
        configure.execute(this)
    }

    fun fetch(href: String, configure: Action<in LinkAsset> = Action {}) = asset(href) {
        asType.convention("fetch")
        crossorigin.convention(true)
        configure.execute(this)
    }

    private fun newAsset(
        href: String,
        configure: Action<in LinkAsset>,
    ): LinkAsset = objects.newInstance(LinkAsset::class.java).apply {
        this.href.set(href)
        rel.set("prefetch")
        crossorigin.convention(false)
        configure.execute(this)
    }
}
