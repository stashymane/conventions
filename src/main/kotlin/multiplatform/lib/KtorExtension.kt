package multiplatform.lib

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class KtorCompilationExtension {
    abstract val mainClass: Property<String>
}

abstract class KtorExtension @Inject constructor(objects: ObjectFactory) {
    val main: KtorCompilationExtension = objects.newInstance(KtorCompilationExtension::class.java)
    val debug: KtorCompilationExtension = objects.newInstance(KtorCompilationExtension::class.java)

    fun main(configure: Action<KtorCompilationExtension>) = configure.execute(main)
    fun debug(configure: Action<KtorCompilationExtension>) = configure.execute(debug)
}
