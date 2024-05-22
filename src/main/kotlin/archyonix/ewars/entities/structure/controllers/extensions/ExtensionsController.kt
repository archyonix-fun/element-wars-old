package archyonix.ewars.entities.structure.controllers.extensions

import archyonix.ewars.entities.region.Region3D
import archyonix.ewars.entities.region.api.Region
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.extensions.events.StructureExtensionConstructEvent
import archyonix.ewars.entities.structure.controllers.extensions.events.StructureExtensionDeconstructEvent
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.shared.libs.AppSchema
import net.minestom.server.coordinate.Point
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class ExtensionsController(
    structure: Structure,
    config: Config
) : Structure.Controller<ExtensionsController, ExtensionsController.Config>(structure, config) {
    private val extensions: MutableSet<Extension> = run {
        val extensions = structure.getProperty<Set<Extension>>("extensions")
            ?: return@run HashSet()
        return@run extensions as MutableSet<Extension>
    }

    // Function
    fun findById(id: String): Extension? = extensions.find { it.id == id.uppercase() }
    fun findFromPoint(point: Point): Extension? = extensions.find { it.region.contains(point) }
    fun getOrCreateExtension(id: String, direction: Direction = Direction.SOUTH): Extension {
        return findById(id) ?: run {
            val config = this.config.extensionConfigMap[id.uppercase()]
                ?: throw RuntimeException("Unknown extension with id: ${id.uppercase()}")
            val extension = Extension(structure, direction, config)
            extensions.add(extension); save()
            return@run extension
        }
    }

    fun deconstruct(id: String) {
        val extension = findById(id)
        extension?.deconstruct() ?: return
        extensions.remove(extension); save()
    }

    fun deconstructAll() {
        extensions.forEach { it.deconstruct() }
        extensions.clear(); save()
    }

    private fun save() = structure.setProperty("extensions", extensions)

    // Child classes
    @ClassLibrary.RegisteredClass(name = "EXTENSIONS", ClassLibrary.Type.STRUCTURE_CONTROLLER)
    class Config(
        config: ConfigurationSection
    ) : Structure.Controller.Config<ExtensionsController, Config>("EXTENSIONS") {
        val extensionConfigMap: Map<String, Extension.Config> = run {
            val configMap: MutableMap<String, Extension.Config> = HashMap()
            for (id in config.getKeys(false)) {
                val section = config.getConfigurationSection(id) ?: continue
                configMap[id] = Extension.Config(section)
            }
            return@run configMap
        }

        override fun getController(structure: Structure): ExtensionsController =
            ExtensionsController(structure, this)
    }

    class Extension(
        private val parent: Structure,
        private val direction: Direction,
        config: Config
    ) {
        private val area = parent.config.area
        private var schema: AppSchema? = null
        val id = config.id
        val region = config.region
            .rotateY(parent.direction, area.size)
            .offset(parent.buildPosition)
        // Functions

        fun construct(schema: AppSchema) {
            val extensionStartBuildingEvent = StructureExtensionConstructEvent(parent, this)
            parent.config.handler.call(extensionStartBuildingEvent)
            schema.build(game.instance, direction, region.startPoint(), parent.team)
            this.schema = schema
        }

        fun deconstruct(): Boolean {
            val extensionDeconstructEvent = StructureExtensionDeconstructEvent(parent, this)
            parent.config.handler.call(extensionDeconstructEvent)
            if (extensionDeconstructEvent.isCancelled) return false
            schema?.fillWithOutAir(game.instance, region.startPoint(), direction, Block.AIR)
            return true
        }

        // Child classes
        class Config(
            config: ConfigurationSection
        ) {
            val id: String = config.name.uppercase()
            val region: Region = Region3D(config)
        }
    }
}