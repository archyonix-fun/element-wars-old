package archyonix.ewars.features.classlibrary

import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.reflections.Reflections
import java.lang.reflect.Constructor

class ClassLibrary {
    private var library: MutableMap<String, Class<*>> = HashMap()

    init {
        val reflections = Reflections("archyonix.ewars")
        reflections.getTypesAnnotatedWith(RegisteredClass::class.java)
            .forEach {
                val registrationData = it.getAnnotation(RegisteredClass::class.java)
                val id = "${registrationData.name}-${registrationData.type.name}"
                library[id] = it
            }
        println("Registered ${library.size} classes")
    }

    fun <T> findClass(name: String, type: Type): Class<T>? {
        val id = "${name}-${type.name}"
        val foundClass = library[id] ?: return null

        @Suppress("UNCHECKED_CAST")
        return foundClass as Class<T>
    }

    fun <T> findConstructor(name: String, type: Type): Constructor<T>? {
        val foundedClass = findClass<T>(name, type) ?: return null
        return foundedClass.getDeclaredConstructor(*type.constructorArgs)
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class RegisteredClass(
        val name: String,
        val type: Type
    )

    enum class Type(
        val constructorArgs: Array<Class<*>> = emptyArray()
    ) {
        ACTION_EXECUTOR(arrayOf(Map::class.java)),
        RESOURCE_EXTRACTOR(arrayOf(ConfigurationSection::class.java)),
        STRUCTURE_CONTROLLER(arrayOf(ConfigurationSection::class.java)),
    }
}