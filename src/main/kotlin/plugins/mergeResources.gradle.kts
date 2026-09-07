package plugins

import org.gradle.kotlin.dsl.create
import tasks.resources.MergeResourcesExtension

extensions.create<MergeResourcesExtension>("mergeResources")
