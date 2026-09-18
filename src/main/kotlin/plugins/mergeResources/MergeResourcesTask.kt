package plugins.mergeResources

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class MergeResourcesTask : DefaultTask() {
    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @get:Inject
    abstract val objects: ObjectFactory

    @get:Nested
    abstract val directories: ListProperty<MergeResourceDirectory>

    @get:OutputDirectory
    abstract val destinationDir: DirectoryProperty

    fun from(directory: Directory) {
        from(objects.directoryProperty().value(directory))
    }

    fun from(directory: Directory, configure: Action<in MergeResourceDirectory>) {
        from(objects.directoryProperty().value(directory), configure)
    }

    fun from(directory: Provider<Directory>) {
        from(directory, Action {})
    }

    fun from(directory: Provider<Directory>, configure: Action<in MergeResourceDirectory>) {
        val entry = objects.newInstance(MergeResourceDirectory::class.java)
        entry.directory.set(directory)
        configure.execute(entry)
        directories.add(entry)
    }

    @TaskAction
    fun merge() {
        fileSystemOperations.sync {
            into(destinationDir)
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            dirPermissions {
                unix("rwxr-xr-x")
            }
            filePermissions {
                unix("rw-r--r--")
            }

            directories.get().forEach { entry ->
                val sourceDir = entry.directory.orNull ?: return@forEach
                val source = sourceDir.asFile.takeIf(File::exists) ?: return@forEach

                from(source) {
                    val intoPath = entry.into.orNull
                    if (!intoPath.isNullOrEmpty()) {
                        into(intoPath)
                    }
                }
            }
        }
    }
}
