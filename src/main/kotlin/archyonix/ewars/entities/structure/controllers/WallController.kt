package archyonix.ewars.entities.structure.controllers

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.region.Region3D
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.shared.libs.AppSchema
import archyonix.ewars.shared.libs.config.Properties
import archyonix.ewars.shared.libs.withDirection
import net.minestom.server.entity.EntityType
import net.minestom.server.instance.block.Block
import net.minestom.server.tag.Tag
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*

@ClassLibrary.RegisteredClass(name = "WALLS", ClassLibrary.Type.STRUCTURE_CONTROLLER)
class WallController(
    config: ConfigurationSection
) : Structure.Config.Controller<WallController.StructureController>("WALLS", setOf("EXTENSIONS")) {
    val config: Config = Config(config)
    private lateinit var extensions: ExtensionsController

    override fun init(structureConfig: Structure.Config) {
        this.extensions = structureConfig.getController("EXTENSIONS")
            ?: throw RuntimeException("Controller with id `EXTENSIONS` was not found")
    }

    override fun getStructureController(structure: Structure): StructureController {
        return StructureController(structure, this)
    }

    // Child classes
    class StructureController(
        private val structure: Structure,
        private val controller: WallController
    ) {
        private val structureExtensions = controller.extensions.getStructureController(structure)
        private val schemasPath = "config/structures/${structure.id}/schemas/"
        private val walls: Array<Config.WallData> = run {
            return@run structure.getProperty<Array<Config.WallData>>("walls")
                ?: return@run Array<Config.WallData>(8) { Config.WallData(Config.WallData.Type.NONE) }
        }

        fun modify(direction: Config.DirectionSettings.Direction, type: Config.WallData.Type) {}

        private fun getOrCreateExtension(id: String, direction: Direction): Structure.Extension {
            return structureExtensions.getExtension(id)
                ?: structureExtensions.createExtension(id, direction)
        }

        private fun construct(direction: Config.DirectionSettings.Direction, type: Config.WallData.Type) {
            val directionSettings = controller.config.directions[direction.name]
                ?: throw RuntimeException("Unregistered direction with name: ${direction.name}")
            val extension = getOrCreateExtension(directionSettings.extension, direction.minecraftDirection)
            val sample = controller.config.samples[directionSettings.sample]
                ?: throw RuntimeException("Sample with id `${directionSettings.sample}` is not exists")
            val variant = structure.getVariant()
                ?: throw RuntimeException("Structure Variant is null")

            when (type) {
                Config.WallData.Type.WALL -> {
                    val schema = AppSchema("${schemasPath}${sample.getSchema(variant)}")
                    walls[direction.index] = Config.WallData(Config.WallData.Type.WALL)
                    extension.build(schema)
                    save()
                }

                Config.WallData.Type.GATE -> {
                    val gateSettings = sample.gateSettings
                        ?: throw RuntimeException("Gate is not configured for this sample")
                    val schema = AppSchema("${schemasPath}${gateSettings.getSchema(variant)}")
                    val block = Block.fromNamespaceId(gateSettings.getBlock(variant))
                    val openingDistance = gateSettings.openingDistance
                    val region3D = run {
                        val firstPoint = structure.buildPosition.add(gateSettings.startPoint.asPoint())
                            .withDirection(direction.minecraftDirection, structure.config.area.size.toShort())
                        val secondPoint = structure.buildPosition.add(gateSettings.startPoint.asPoint())
                            .withDirection(direction.minecraftDirection, structure.config.area.size.toShort())
                        return@run Region3D(firstPoint, secondPoint).normalize()
                    }

                    val wallData = Config.WallData(Config.WallData.Type.WALL)

                    wallData.setProperty("uuid", UUID.randomUUID())
                    wallData.setProperty("gateBlock", block)
                    wallData.setProperty("openingDistance", openingDistance)
                    wallData.setProperty("region", region3D)
                    wallData.setProperty("isOpen", false)

                    walls[direction.index] = wallData
                    extension.build(schema)
                    save()
                }

                else -> throw RuntimeException("It is impossible to build: NONE")
            }
        }

        fun deconstruct(direction: Config.DirectionSettings.Direction) {
            val current = walls[direction.index]
            if (current.type == Config.WallData.Type.NONE) return
            val directionSettings = controller.config.directions[direction.name]
                ?: throw RuntimeException("Unregistered direction with name: ${direction.name}")
            val extension = getOrCreateExtension(directionSettings.extension, direction.minecraftDirection)

            if (current.type == Config.WallData.Type.GATE) closeGate(direction)
            walls[direction.index] = Config.WallData(Config.WallData.Type.NONE)
            extension.deconstruct()
        }

        fun openGate(direction: Config.DirectionSettings.Direction) {
            val gate = walls[direction.index]
            if (gate.type != Config.WallData.Type.GATE) return

            val uuid = gate.getProperty<String>("uuid")
                ?: throw RuntimeException("Gate uuid is null")
            val region3D = gate.getProperty<Region3D>("region")
                ?: throw RuntimeException("Gate region is null")

            game.instance.entities
                .filter { it.entityType == EntityType.BLOCK_DISPLAY }
                .filter { it.hasTag(Tag.String("owner")) }
                .filter { it.getTag(Tag.String("owner")) == uuid }
                .forEach { it.remove() }

            Zone.fromRegion3D(region3D).fill(Block.AIR)
        }

        fun closeGate(direction: Config.DirectionSettings.Direction) {
            val gate = walls[direction.index]
            if (gate.type != Config.WallData.Type.GATE) return

            val uuid = gate.getProperty<String>("uuid")
                ?: throw RuntimeException("Gate uuid is null")
            val region3D = gate.getProperty<Region3D>("region")
                ?: throw RuntimeException("Gate region is null")
            // TODO Summon block display with gate block
            Zone.fromRegion3D(region3D).fill(Block.BARRIER)
        }

        private fun save() = structure.setProperty("walls", walls)
    }

    class Config(
        config: ConfigurationSection
    ) {
        val samples: Map<String, Sample> = run {
            val section = config.getConfigurationSection("samples") ?: return@run emptyMap()
            val sampleMap: MutableMap<String, Sample> = HashMap()
            for (sampleId in section.getKeys(false)) {
                val sampleSection = section.getConfigurationSection(sampleId) ?: continue
                sampleMap[sampleId] = Sample(sampleSection)
            }
            return@run sampleMap
        }
        val directions: Map<String, DirectionSettings> = run {
            val section = config.getConfigurationSection("directions") ?: return@run emptyMap()
            val directionMap: MutableMap<String, DirectionSettings> = HashMap()
            for (directionId in section.getKeys(false)) {
                val directionSection = section.getConfigurationSection(directionId) ?: continue
                directionMap[directionId] = DirectionSettings(directionSection)
            }
            return@run directionMap
        }

        // Child classes
        class Sample(
            config: ConfigurationSection
        ) {
            private val schema: String = config.getString("schema")
                ?: throw RuntimeException("Field schema is null")
            val gateSettings: GateSettings? = run {
                val section = config.getConfigurationSection("gateSettings") ?: return@run null
                return@run GateSettings(section)
            }

            fun getSchema(variant: Structure.Config.Variant): String {
                if (!schema.startsWith("$")) return schema
                val key = schema.substring(1)
                return variant.getProperty<String>(key)
                    ?: throw RuntimeException("Variant property with name `$key` is not exists")
            }

            // Child classes
            class GateSettings(
                config: ConfigurationSection
            ) {
                private val schema: String = config.getString("schema")
                    ?: throw RuntimeException("Field schema is null")
                private val block: String = config.getString("block")
                    ?: throw RuntimeException("Field block is null")
                val openingDistance: Int = config.getInt("openingDistance")
                val startPoint = Structure.Config.LocalPosition(
                    config.getConfigurationSection("gate.startPoint")
                        ?: throw RuntimeException("Field gate.startPoint is null")
                )
                val endPoint = Structure.Config.LocalPosition(
                    config.getConfigurationSection("gate.endPoint")
                        ?: throw RuntimeException("Field gate.endPoint is null")
                )

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

        class DirectionSettings(
            config: ConfigurationSection
        ) {
            val direction: Direction = Direction.valueOf(config.name)
            val extension: String = config.getString("extension")
                ?: throw RuntimeException("Field extension is null")
            val sample: String = config.getString("sample")
                ?: throw RuntimeException("Field sample is null")

            enum class Direction(
                val index: Int,
                val minecraftDirection: net.minestom.server.utils.Direction
            ) {
                NORTH_WEST(0, net.minestom.server.utils.Direction.SOUTH),
                NORTH(1, net.minestom.server.utils.Direction.SOUTH),
                EAST_NORTH(2, net.minestom.server.utils.Direction.WEST),
                EAST(3, net.minestom.server.utils.Direction.WEST),
                SOUTH_EAST(4, net.minestom.server.utils.Direction.NORTH),
                SOUTH(5, net.minestom.server.utils.Direction.NORTH),
                WEST_SOUTH(6, net.minestom.server.utils.Direction.EAST),
                WEST(7, net.minestom.server.utils.Direction.EAST);

                fun reflect() = when (this) {
                    NORTH_WEST -> SOUTH_EAST
                    NORTH -> SOUTH
                    EAST_NORTH -> WEST_SOUTH
                    EAST -> WEST
                    SOUTH_EAST -> NORTH_WEST
                    SOUTH -> NORTH
                    WEST_SOUTH -> EAST_NORTH
                    WEST -> EAST
                }
            }
        }

        class WallData(
            val type: Type
        ) : Properties() {
            enum class Type { NONE, WALL, GATE }
        }
    }
}