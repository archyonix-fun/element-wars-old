package archyonix.ewars.entities.`$structure`.controllers

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.features.classlibrary.ClassLibrary
import net.minestom.server.coordinate.Point
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@ClassLibrary.RegisteredClass(name = "EXTENSIONS", ClassLibrary.Type.STRUCTURE_CONTROLLER)
open class ExtensionsController(
    config: ConfigurationSection
) : Structure.Config.Controller("EXTENSIONS") {
    private val configMap: Map<String, Structure.Extension.Config> = run {
        val extensions: MutableMap<String, Structure.Extension.Config> = HashMap()
        for (id in config.getKeys(false)) {
            val section = config.getConfigurationSection(id) ?: continue
            extensions[id] = Structure.Extension.Config(section)
        }
        return@run extensions
    }

    fun getConfig(extensionId: String) =
        configMap[extensionId] ?: throw RuntimeException("Config with id `$extensionId` is not exists")

    fun getStructureController(structure: Structure): StructureController {
        return StructureController(structure, this)
    }

    // Child classes
    class StructureController(
        private val structure: Structure,
        private val controller: ExtensionsController
    ) {
        private val extensionMap: MutableMap<String, Structure.Extension> = run {
            val extensions = structure
                .getProperty<Map<String, Structure.Extension>>("extensions") ?: return@run HashMap()
            return@run extensions as MutableMap<String, Structure.Extension>
        }

        fun hasExtension(id: String) = extensionMap.containsKey(id)
        fun getExtension(id: String) = extensionMap[id]
        fun findExtension(point: Point) =
            extensionMap.values.find { it.region3D.contains(point) }

        fun createExtension(id: String, direction: Direction): Structure.Extension {
            val config = controller.getConfig(id)
            val position = structure.buildPosition.add(config.position.asPoint())
            val extension = Structure.Extension(structure, config, position, direction)
            extensionMap[id] = extension
            save()

            return extension
        }

        fun removeExtension(id: String): Boolean {
            val extension = getExtension(id) ?: return false
            extension.deconstruct()
            extensionMap.remove(id)
            save()

            return true
        }

        fun removeAll() {
            for (extension in extensionMap.values)
                extension.deconstruct()
            extensionMap.clear()
            save()
        }

        private fun save() = structure.setProperty("extensions", extensionMap)
    }
}