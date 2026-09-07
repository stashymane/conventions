package tasks.resources

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * A source directory to merge into [MergeResourcesTask.destinationDir].
 *
 * Missing directories are skipped at execution time.
 * When [into] is set, contents are placed under that relative path in the destination.
 */
abstract class MergeResourceDirectory {
    @get:InputDirectory
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val directory: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val into: Property<String>
}
