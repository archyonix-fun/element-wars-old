package archyonix.ewars.entities.`$structure`

import archyonix.ewars.entities.`$structure`.events.*
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.team.GameTeam
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class StructureManager {
    private val configMap: MutableMap<String, Structure.Config> = HashMap()
    private val structureSet: MutableSet<Structure> = HashSet()

    companion object {
        val STRUCTURES_DIR = File("config/structures")
    }

    fun getConfig(structureId: String) =
        configMap[structureId] ?: throw RuntimeException("Config with id `$structureId` is not exists")

    fun findStructures(team: GameTeam): Set<Structure> =
        structureSet.filter { it.team == team }.toSet()

    fun findStructures(team: GameTeam, structureId: String): Set<Structure> =
        findStructures(team).filter { it.id == structureId }.toSet()

    fun createStructure(structureId: String, builder: Player): Boolean {
        val structureConfig = getConfig(structureId)
        val structureTeam = builder.gameTeam()

        if (!structureConfig.checkBuildingLimit(structureTeam)) {
            builder.showActionBar("Достигнут лимит на количество подобных зданий", MessageType.WARNING)
            return false
        }

        val calcPositionEvent = CalculateStructurePositionEvent(structureConfig, builder)
        structureConfig.handler.call(calcPositionEvent)
        val position = calcPositionEvent.resultPosition
            ?: throw RuntimeException("Calculation Error: position is null")

        val calcDirectionEvent = CalculateStructureDirectionEvent(structureConfig, builder)
        structureConfig.handler.call(calcDirectionEvent)
        val direction = calcDirectionEvent.resultDirection
            ?: throw RuntimeException("Calculation Error: direction is null")

        val region2D = Region2D.fromSizeArea(position, structureConfig.area.size)

        val calcBuildZoneEvent = CalculateBuildZoneEvent(structureConfig, position.blockY(), region2D)
        structureConfig.handler.call(calcBuildZoneEvent)
        val buildZoneAir = calcBuildZoneEvent.resultAirZone
            ?: throw RuntimeException("Calculation Error: air zone is null")
        val buildZoneBase = calcBuildZoneEvent.resultBaseZone
            ?: throw RuntimeException("Calculation Error: base zone is null")

        val validateBuildZoneEvent = ValidateBuildZoneEvent(structureConfig, buildZoneAir, buildZoneBase)
        structureConfig.handler.call(validateBuildZoneEvent)
        if (!validateBuildZoneEvent.isValid) {
            builder.showActionBar(validateBuildZoneEvent.getErrorMessage(), MessageType.WARNING)
            return false
        }

        structureConfig.handler
            .call(PrepareBuildZoneEvent(structureConfig, buildZoneAir, buildZoneBase))

        val structure = Structure(structureId, position, direction, builder.gameTeam())
        structureSet.add(structure)

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
                    throw RuntimeException("Structures dir: ($dir) - ${e.localizedMessage}")
                }
            }
        }
    }
}