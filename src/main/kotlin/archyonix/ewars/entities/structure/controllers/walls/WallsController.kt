package archyonix.ewars.entities.structure.controllers.walls

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.region.Region3D
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.entities.region.api.Region
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.extensions.ExtensionsController
import archyonix.ewars.entities.structure.events.StructureCalculateDirectionEvent
import archyonix.ewars.entities.structure.events.StructureDeconstructEvent
import archyonix.ewars.entities.structure.events.StructureUpdateEvent
import archyonix.ewars.entities.structure.functions.showRegion
import archyonix.ewars.entities.structure.types.StructureOffset
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.shared.libs.AppSchema
import archyonix.ewars.shared.libs.config.Properties
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*

class WallsController(
    structure: Structure,
    config: Config
) : Structure.Controller<WallsController, WallsController.Config>(structure, config) {
    private val schemasPath = "config/structures/${structure.id}/schemas/"
    private val extensions = config.extensions().getController(structure)
    private val places: Array<Place> = run {
        return@run structure.getProperty<Array<Place>>("places")
            ?: return@run Array<Place>(8) { Place() }
    }

    // Functions
    fun sampleTypeByOffset(offset: StructureOffset.Global2D) = places[offset.index].sampleType
    fun offsetByPoint(point: Point): StructureOffset.Global2D? {
        if (!structure.region3D.contains(point)) return null
        for (offset in StructureOffset.Global2D.values()) {
            if (extensionFromOffset(offset).region.contains(point))
                return offset
        }
        return null
    }

    fun showPlaceByOffset(player: Player, offset: StructureOffset.Global2D) {
        val extension = extensionFromOffset(offset)
        showRegion(extension.region as Region3D, player)
    }

    fun modify(offset: StructureOffset.Global2D, sampleType: Place.SampleType): Boolean {
        val neighbor = game.structureManager.findNeighborFromOffset(structure, offset) ?: return false
        if (neighbor.id != structure.id) return false
        val neighborController = neighbor.config
            .getController(Config::class.java)
            ?.getController(neighbor)
            ?: return false

        when (sampleType) {
            Place.SampleType.NONE -> {
                deconstruct(offset)
                neighborController.deconstruct(offset.reflect())
            }

            else -> {
                construct(offset, sampleType)
                neighborController.construct(offset.reflect(), sampleType)
            }
        }
        return true
    }

    fun openGate(offset: StructureOffset.Global2D) {
        val place = places[offset.index]
        if (place.sampleType != Place.SampleType.GATE) return

        val region = place.getProperty<Region>("region")
            ?: throw RuntimeException("Gate region is null")

        place.setProperty("isOpen", true)
        Zone(region).fill(Block.AIR)
    }

    fun closeGate(offset: StructureOffset.Global2D) {
        val place = places[offset.index]
        if (place.sampleType != Place.SampleType.GATE) return

        val region = place.getProperty<Region>("region")
            ?: throw RuntimeException("Gate region is null")

        place.setProperty("isOpen", false)
        Zone(region).fill(Block.BARRIER)
    }

    private fun extensionFromOffset(offset: StructureOffset.Global2D): ExtensionsController.Extension {
        val placeConfig = config.places[offset.name]
            ?: throw RuntimeException("Unregistered place with name: ${offset.name}")
        return extensions.getOrCreateExtension(placeConfig.extensionId, offset.direction)
    }

    private fun construct(offset: StructureOffset.Global2D, sampleType: Place.SampleType) {
        val placeConfig = config.places[offset.name]
            ?: throw RuntimeException("Unregistered place with name: ${offset.name}")
        val extension = extensions.getOrCreateExtension(placeConfig.extensionId, offset.direction)
        val sample = config.samples[placeConfig.sampleId]
            ?: throw RuntimeException("Sample with id `${placeConfig.sampleId}` is not exists")
        val variant = structure.getVariant()
            ?: throw RuntimeException("Structure Variant is null")

        deconstruct(offset)
        when (sampleType) {
            Place.SampleType.WALL -> {
                val schema = AppSchema("${schemasPath}${sample.getSchema(variant)}")
                places[offset.index] = Place(Place.SampleType.WALL)
                extension.construct(schema)
                save()
            }

            Place.SampleType.GATE -> {
                val gateConfig = sample.gateConfig
                    ?: throw RuntimeException("Gate is not configured for this sample")
                val schema = AppSchema("${schemasPath}${gateConfig.getSchema(variant)}")
                val block = Block.fromNamespaceId(gateConfig.getBlock(variant))
                val openDistance = gateConfig.openDistance
                val region = gateConfig.region
                    .rotateY(offset.direction, structure.config.area.size)
                    .offset(structure.buildPosition)

                val place = Place(Place.SampleType.GATE)
                place.setProperty("uuid", UUID.randomUUID())
                place.setProperty("block", block)
                place.setProperty("openDistance", openDistance)
                place.setProperty("region", region)
                place.setProperty("isOpen", false)

                places[offset.index] = place
                extension.construct(schema)
                save()
            }

            else -> throw RuntimeException("Please use deconstruct instead")
        }
    }

    private fun deconstruct(offset: StructureOffset.Global2D) {
        val current = places[offset.index]
        if (current.sampleType == Place.SampleType.NONE) return
        val placeConfig = config.places[offset.name]
            ?: throw RuntimeException("Unregistered direction with name: ${offset.name}")
        val extension = extensions.getOrCreateExtension(placeConfig.extensionId, offset.direction)
        places[offset.index] = Place()
        extension.deconstruct()
        save()
    }

    private fun deconstructAll() {
        for (offset in StructureOffset.Global2D.values()) {
            modify(offset, Place.SampleType.NONE)
        }
    }

    private fun save() = structure.setProperty("places", places)

    // Child classes
    @ClassLibrary.RegisteredClass(name = "WALLS", ClassLibrary.Type.STRUCTURE_CONTROLLER)
    class Config(
        config: ConfigurationSection
    ) : Structure.Controller.Config<WallsController, Config>("WALLS", setOf("EXTENSIONS")) {
        private var extensions: ExtensionsController.Config? = null
        val samples: Map<String, Sample> = run {
            val section = config.getConfigurationSection("samples") ?: return@run emptyMap()
            val sampleMap: MutableMap<String, Sample> = HashMap()
            for (sampleId in section.getKeys(false)) {
                val sampleSection = section.getConfigurationSection(sampleId) ?: continue
                sampleMap[sampleId] = Sample(sampleSection)
            }
            return@run sampleMap
        }
        val places: Map<String, Place.Config> = run {
            val section = config.getConfigurationSection("places") ?: return@run emptyMap()
            val directionMap: MutableMap<String, Place.Config> = HashMap()
            for (directionId in section.getKeys(false)) {
                val directionSection = section.getConfigurationSection(directionId) ?: continue
                directionMap[directionId] = Place.Config(directionSection)
            }
            return@run directionMap
        }

        init {
            addListener(StructureUpdateEvent::class.java) { event ->
                val structure = event.structure
                if (structure.getProcess().type != Structure.Process.Type.IDLE) return@addListener

                val nearbyPlayers = game.getPlayers()
                    .filter { it.gameTeam() == structure.team }
                    .filter { it.position.distance(structure.position) <= structure.config.area.size }
                    .filter { it.itemInMainHand.hasGameItem(GameItem.CONSTRUCTION_HAMMER) }
                if (nearbyPlayers.isEmpty()) return@addListener

                val controller = getController(structure)
                for (offset in StructureOffset.Global2D.values()) {
                    if (controller.sampleTypeByOffset(offset) != Place.SampleType.NONE) continue
                    val neighbor = game.structureManager.findNeighborFromOffset(structure, offset) ?: continue
                    if (neighbor.getProcess().type != Structure.Process.Type.IDLE) continue
                    nearbyPlayers.forEach { controller.showPlaceByOffset(it, offset) }
                }
            }
            addListener(StructureDeconstructEvent::class.java) { event ->
                getController(event.structure).deconstructAll()
            }
            addListener(StructureCalculateDirectionEvent::class.java) { event ->
                event.resultDirection = Direction.SOUTH
            }
        }

        // Functions
        fun extensions(): ExtensionsController.Config = extensions!!
        override fun init(structureConfig: Structure.Config) {
            this.extensions = structureConfig.getController(ExtensionsController.Config::class.java)
                ?: throw RuntimeException("Dependency not found: EXTENSIONS")
        }

        override fun getController(structure: Structure): WallsController =
            WallsController(structure, this)

        // Child classes
        class Sample(
            config: ConfigurationSection
        ) {
            private val schema: String = config.getString("schema")
                ?: throw RuntimeException("Field schema is null")
            val gateConfig: Gate? = run {
                val section = config.getConfigurationSection("gateConfig") ?: return@run null
                return@run Gate(section)
            }

            fun getSchema(variant: Structure.Config.Variant): String {
                if (!schema.startsWith("$")) return schema
                val key = schema.substring(1)
                return variant.getProperty<String>(key)
                    ?: throw RuntimeException("Variant property with name `$key` is not exists")
            }

            // Child classes
            class Gate(
                config: ConfigurationSection
            ) {
                private val schema: String = config.getString("schema")
                    ?: throw RuntimeException("Field schema is null")
                private val block: String = config.getString("block")
                    ?: throw RuntimeException("Field block is null")
                val openDistance: Int = config.getInt("openDistance")
                val region: Region = Region3D(
                    config.getConfigurationSection("region")
                        ?: throw RuntimeException("Field region is null")
                )

                // Functions
                fun getSchema(variant: Structure.Config.Variant): String {
                    if (!schema.startsWith("$")) return schema
                    val key = schema.substring(1)
                    return variant.getProperty<String>(key)
                        ?: throw RuntimeException("Variant property with name `$key` is not exists")
                }

                fun getBlock(variant: Structure.Config.Variant): String {
                    if (!block.startsWith("$")) return schema
                    val key = block.substring(1)
                    return variant.getProperty<String>(key)
                        ?: throw RuntimeException("Variant property with name `$key` is not exists")
                }
            }
        }
    }

    // Child classes
    class Place(val sampleType: SampleType = SampleType.NONE) : Properties() {
        // Child classes
        class Config(
            config: ConfigurationSection
        ) {
            val offset = StructureOffset.Global2D.valueOf(config.name)
            val extensionId: String = config.getString("extension")
                ?: throw RuntimeException("Field extension is null")
            val sampleId: String = config.getString("sample")
                ?: throw RuntimeException("Field sample is null")
        }

        enum class SampleType { NONE, WALL, GATE }
    }
}