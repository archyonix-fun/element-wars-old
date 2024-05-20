package archyonix.ewars.entities.structure

import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.events.*
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.entities.team.GameTeam
import net.minestom.server.coordinate.Point
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

    fun findStructures(team: GameTeam): Set<Structure> =
        structureSet.filter { it.team == team }.toSet()

    fun findStructures(team: GameTeam, structureId: String): Set<Structure> =
        findStructures(team).filter { it.id == structureId }.toSet()

    fun findStructure3D(blockPosition: Point): Structure? =
        structureSet.find { it.region3D.contains(blockPosition) }

    fun findStructure2D(blockPosition: Point): Structure? =
        structureSet.find { it.region2D.contains(blockPosition) }

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

        val calcPositionEvent = CalculateStructurePositionEvent(structureConfig, builder)
        structureConfig.handler.call(calcPositionEvent)
        val position = (calcPositionEvent.resultPosition
            ?: throw RuntimeException("Calculation Error: position is null"))
            .withGrid(structureConfig.grid)

        val calcDirectionEvent = CalculateStructureDirectionEvent(structureConfig, builder)
        structureConfig.handler.call(calcDirectionEvent)
        val direction = calcDirectionEvent.resultDirection
            ?: throw RuntimeException("Calculation Error: direction is null")

        val region2D = Region2D.fromSizeArea(position, structureConfig.area.size)

        if (!validateRegion(region2D)) {
            builder.showActionBar("Другая постройка мешает строительству", MessageType.WARNING)
            return false
        }

        val calcBuildZoneEvent = CalculateBuildZoneEvent(structureConfig, position.blockY(), region2D)
        structureConfig.handler.call(calcBuildZoneEvent)
        val buildZoneAir = calcBuildZoneEvent.resultAirZone
            ?: throw RuntimeException("Calculation Error: air zone is null")
        val buildZoneBase = calcBuildZoneEvent.resultBaseZone
            ?: throw RuntimeException("Calculation Error: base zone is null")

        val validateBuildZoneEvent = ValidateBuildZoneEvent(structureConfig, buildZoneAir, buildZoneBase)
        structureConfig.handler.call(validateBuildZoneEvent)
        if (!validateBuildZoneEvent.isValid) {
            builder.showActionBar(validateBuildZoneEvent.getWarningMessage(), MessageType.WARNING)
            return false
        }

        structureConfig.handler
            .call(PrepareBuildZoneEvent(structureConfig, buildZoneAir, buildZoneBase))

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