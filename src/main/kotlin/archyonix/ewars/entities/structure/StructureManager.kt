package archyonix.ewars.entities.structure

import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.events.*
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.entities.structure.types.StructureOffset
import archyonix.ewars.entities.team.GameTeam
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class StructureManager {
    private val configMap: MutableMap<String, Structure.Config> = HashMap()
    private val structureSet: MutableSet<Structure> = HashSet()

    companion object {
        val STRUCTURES_DIR = File("config/structures")
    }

    fun configMap(): Map<String, Structure.Config> = configMap
    fun getConfig(structureId: String) =
        configMap[structureId] ?: throw RuntimeException("Config with id `$structureId` is not exists")

    fun updateStructures() = structureSet.forEach(Structure::update)

    fun findStructures(team: GameTeam): Set<Structure> =
        structureSet.filter { it.team == team }.toSet()

    fun findStructures(team: GameTeam, structureId: String): Set<Structure> =
        findStructures(team).filter { it.id == structureId }.toSet()

    fun findStructure3D(blockPosition: Point): Structure? =
        structureSet.find { it.region3D.contains(blockPosition) }

    fun findStructure2D(blockPosition: Point): Structure? =
        structureSet.find { it.region2D.contains(blockPosition) }

    fun findNeighbors(structure: Structure): Set<Structure> {
        val neighborsSet: MutableSet<Structure> = HashSet()
        for (offset in StructureOffset.Global2D.values())
            neighborsSet.add(findNeighborFromOffset(structure, offset) ?: continue)
        return neighborsSet
    }

    fun findNeighborFromOffset(structure: Structure, offset: StructureOffset.Global2D): Structure? {
        val area = structure.config.area
        val isEven = area.size % 2 == 0
        val position = offset.vector.mul(area.radius.toDouble())
        val correctX = if (position.blockX() > 0 && isEven) 0.0 else 1.0
        val correctZ = if (position.blockZ() > 0 && isEven) 0.0 else 1.0
        val point = structure.position.add(position.sub(Vec(correctX, correctZ)))
        val foundedStructure = findStructure2D(point)
        return if (foundedStructure != structure) foundedStructure else null
    }


    fun createStructure(structureId: String, builder: Player): Boolean {
        val structureConfig = getConfig(structureId)
        val structureTeam = builder.gameTeam()

        if (!structureConfig.checkBuildingLimit(structureTeam)) {
            builder.showActionBar("Достигнут лимит на количество подобных зданий", MessageType.WARNING)
            return false
        }

        if (!structureConfig.defaultVariant.checkDependencies(structureTeam)) {
            builder.showActionBar("Не все тебования выполнены", MessageType.WARNING)
            return false
        }

        val calcPositionEvent = StructureCalculatePositionEvent(structureConfig, builder)
        structureConfig.handler.call(calcPositionEvent)
        val position = (calcPositionEvent.resultPosition
            ?: throw RuntimeException("Calculation Error: position is null"))
            .withGrid(structureConfig.grid)

        val calcDirectionEvent = StructureCalculateDirectionEvent(structureConfig, builder)
        structureConfig.handler.call(calcDirectionEvent)
        val direction = calcDirectionEvent.resultDirection
            ?: throw RuntimeException("Calculation Error: direction is null")

        val region2D = Region2D(position, structureConfig.area)

        if (!validateRegion(region2D)) {
            builder.showActionBar("Другая постройка мешает строительству", MessageType.WARNING)
            return false
        }

        val calcBuildZoneEvent = StructureCalculateBuildZoneEvent(structureConfig, position.blockY(), region2D)
        structureConfig.handler.call(calcBuildZoneEvent)
        val buildZoneAir = calcBuildZoneEvent.resultAirZone
            ?: throw RuntimeException("Calculation Error: air zone is null")
        val buildZoneBase = calcBuildZoneEvent.resultBaseZone
            ?: throw RuntimeException("Calculation Error: base zone is null")

        val validateBuildZoneEvent = StructureValidateBuildZoneEvent(structureConfig, buildZoneAir, buildZoneBase)
        structureConfig.handler.call(validateBuildZoneEvent)
        if (!validateBuildZoneEvent.isValid) {
            builder.showActionBar(validateBuildZoneEvent.getWarningMessage(), MessageType.WARNING)
            return false
        }

        structureConfig.handler
            .call(StructurePrepareBuildZoneEvent(structureConfig, buildZoneAir, buildZoneBase))

        val structure = Structure(structureId, position, direction, builder.gameTeam(), buildZoneAir)
        structureSet.add(structure)

        val startBuildingEvent = StructurePlaceEvent(structure, structureConfig.defaultVariant)
        structureConfig.handler.call(startBuildingEvent)
        structure.changeVariant(startBuildingEvent.resultVariant)

        return true
    }

    fun deconstructStructure(structure: Structure): Boolean {
        if (!structure.deconstruct()) return false
        structureSet.remove(structure)
        return true
    }

    private fun validateRegion(region2D: Region2D): Boolean {
        for (it in structureSet) {
            if (it.region2D.contains(region2D)) return false
        }
        return true
    }

    init {
        if (STRUCTURES_DIR.exists() && STRUCTURES_DIR.isDirectory) {
            for (dir in STRUCTURES_DIR.listFiles()!!) {
                try {
                    val config = YamlConfiguration.loadConfiguration(File(dir, "structure.yaml"))
                    val structureConfig = Structure.Config(config)
                    configMap[structureConfig.id] = structureConfig
                } catch (e: Exception) {
                    throw RuntimeException("Structures dir: ($dir) - ${e.localizedMessage}", e)
                }
            }
        }
    }
}