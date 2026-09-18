package plugins.preload

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * A single `<link>` asset to inject into the browser distribution `index.html`.
 */
abstract class LinkAsset {
    @get:Input
    abstract val href: Property<String>

    /**
     * Link relation: `preload`, `prefetch`, or `modulepreload`.
     */
    @get:Input
    abstract val rel: Property<String>

    /**
     * Value for the HTML `as` attribute (`script`, `style`, `image`, `font`, `fetch`, …).
     * Unused for `modulepreload`.
     */
    @get:Input
    @get:Optional
    abstract val asType: Property<String>

    @get:Input
    @get:Optional
    abstract val type: Property<String>

    @get:Input
    abstract val crossorigin: Property<Boolean>
}
