package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.StructureTower
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.features.action.Action
import archyonix.ewars.game.functions.ActionBar
import archyonix.ewars.structureManager
import net.minestom.server.entity.Player

@Action.RegisteredExecutor("BUILD/REPAIR")
class BuildExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player): Boolean {
        val itemStack = player.itemInMainHand
        if (!itemStack.hasCharged()) return false

        val variant = player.itemInMainHand.asItemVariant() ?: return false
        val point = player.getTargetBlockPosition(5) ?: return false
        val structure = structureManager.findStructure<Structure>(point) ?: return false
        if (structure.team != player.gameTeam()) return false

        val buildPower = (variant.properties["buildPower"] ?: 0) as Int

        structure.buildOrRepair(buildPower)
        ActionBar(ActionBar.Progress(structure.getBuildProgress())).show(player)
        when (structure) {
            is StructureTower -> {
                if (structure.hasBuilding()) return false
                if (!structure.openWallMenu(player, point)) {
                    structure.buildWall(point.withGrid(4))
                }
            }
        }
        return true
    }
}