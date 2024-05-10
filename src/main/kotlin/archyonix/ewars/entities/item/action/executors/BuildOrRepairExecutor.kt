package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.StructureTower
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game.functions.ActionBar
import archyonix.ewars.structureManager
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*


private val buildCD: MutableMap<UUID, Long> = HashMap()

@Executor("BUILD_OR_REPAIR")
class BuildOrRepairExecutor : IExecutor {
    override fun execute(player: Player, data: ConfigurationSection): Boolean {
        val item = player.itemInMainHand.getGameItem() ?: return false
        val itemData = item.data ?: return false

        val power = itemData.getInt("power")
        val blockPos = (player.getTargetBlockPosition(5) ?: return false) as Vec
        val structure = structureManager.findStructure<Structure>(blockPos.asPosition()) ?: return false
        if (structure.team != player.gameTeam()) return false

        if (!useCD(structure.uuid)) return false

        val itemStack = player.itemInMainHand
        if (!itemStack.hasCharged()) return false

        if (structure.hasTower() && !structure.hasBuilding()) {
            (structure as StructureTower).buildWall(blockPos.withGrid(4))
        } else {
            structure.buildOrRepair(power)
            ActionBar(ActionBar.Progress(structure.getBuildProgress()))
                .show(player)
        }
        return true
    }

    private fun useCD(uuid: UUID): Boolean {
        val currentTime = Date().time
        if (buildCD.contains(uuid)) {
            if (currentTime - buildCD[uuid]!! < 100) {
                buildCD[uuid] = currentTime
                return false
            }
        }
        buildCD[uuid] = currentTime
        return true
    }
}