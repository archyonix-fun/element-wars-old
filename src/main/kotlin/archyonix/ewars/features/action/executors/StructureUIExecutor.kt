package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.features.action.Action
import archyonix.ewars.structureManager
import net.minestom.server.entity.Player

@Action.RegisteredExecutor("STRUCTURE_UI")
class StructureUIExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player): Boolean {
        val point = player.getTargetBlockPosition(5) ?: return false
        val structure = structureManager.findStructure<Structure>(point) ?: return false
        if (structure.hasBuilding()) return false
        if (structure.team != player.gameTeam()) return false
        structure.openUI(player)
        return true
    }
}