package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("STRUCTURE_UI", ClassLibrary.Type.EXECUTOR)
class StructureUIExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player): Boolean {
        val point = player.getTargetBlockPosition(5) ?: return false
        val structure = game.structureManager.findStructure3D(point) ?: return false
        if (structure.getProcess().type != Structure.Process.Type.IDLE) return false
        if (structure.team != player.gameTeam()) return false
        // TODO Structure open ui function
        return true
    }
}