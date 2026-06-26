package multiplatform.plugin

import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension

configure<ComposeExtension> {
    configure<ResourcesExtension> {
        packageOfResClass = project.group.toString()
    }
}
