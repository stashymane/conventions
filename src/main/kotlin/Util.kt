import org.gradle.api.Project

private fun Project.pathComponents(): List<String> = path.substring(1).split(':')

internal fun Project.moduleName(): String =
    path.substring(1).replace(':', '-')

internal fun Project.projectIdentifier(): String =
    (listOf(rootProject.name) + pathComponents())
        .filter(String::isNotEmpty)
        .joinToString(".")

internal fun String.toIntOrElseThrow(orElse: (String) -> Exception) = toIntOrNull() ?: throw orElse(this)
