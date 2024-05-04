package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.getChargeValue
import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.structureManager
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

        return if (structure.team.id == player._getTeam().id) {
            structure.buildOrRepair(power)
            true
        } else false
    }
}