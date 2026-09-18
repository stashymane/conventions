package plugins.mergeResources

import org.gradle.api.Action
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class MergeResourcesExtension @Inject constructor(
    private val tasks: TaskContainer,
    private val layout: ProjectLayout,
) {
    operator fun invoke(
        name: String,
        configure: Action<in MergeResourcesTask> = Action { },
    ): TaskProvider<MergeResourcesTask> = tasks.register(name, MergeResourcesTask::class.java) {
        destinationDir.convention(layout.buildDirectory.dir("mergedResources"))
        configure.execute(this)
    }
}
