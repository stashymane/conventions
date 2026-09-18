package plugins.preload

import org.gradle.api.provider.Property

/**
 * Controls which distribution artifacts are auto-discovered and preloaded.
 */
abstract class DiscoveryConfig {
    /** When true, scan the distribution directory for matching file extensions. */
    abstract val enabled: Property<Boolean>

    /** Preload `*.wasm` as `fetch` with `crossorigin`. Default: true. */
    abstract val wasm: Property<Boolean>

    /** Emit `modulepreload` for `*.mjs`. Default: true. */
    abstract val mjs: Property<Boolean>

    /** Preload `*.js` as `script`. Default: false. */
    abstract val js: Property<Boolean>
}
