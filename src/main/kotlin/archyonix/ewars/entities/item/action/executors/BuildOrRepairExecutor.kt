package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.getChargeValue
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.entities.structure.isTower
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
        val structure = structureManager.findStructure(blockPos.asPosition()) ?: return false
        if (structure.team != player.gameTeam()) return false

        val currentTime = Date().time
        if (buildCD.contains(structure.uuid)) {
            if (currentTime - buildCD[structure.uuid]!! < 100) {
                buildCD[structure.uuid] = currentTime
                return false
            }
        }
        buildCD[structure.uuid] = currentTime

        val itemStack = player.itemInMainHand
        if (itemStack.getChargeValue() < 1f) return false

        if (structure.isTower() && !structure.hasBuilding()) buildWall(blockPos.withGrid(4))
        else {
            structure.buildOrRepair(power)
            ActionBar(
                ActionBar.Progress(structure.getBuildProgress())
            ).show(player)
        }
        return true
    }

    private fun buildWall(pos: Point) {
        val tower = structureManager.findTower(pos) ?: return
        if (tower.structure.hasBuilding()) return
        val direction = tower.findDirection(pos)
        tower.buildWall(direction)
    }
}