import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import java.util.*

fun ProviderFactory.properties(file: Provider<RegularFile>): Provider<Properties> =
    fileContents(file).asText.orElse("").map { text ->
        Properties().apply { load(text.byteInputStream()) }
    }

val Project.localProperties: Provider<Properties>
    get() = providers.properties(rootProject.layout.file(rootProject.provider { rootProject.file("local.properties") }))
