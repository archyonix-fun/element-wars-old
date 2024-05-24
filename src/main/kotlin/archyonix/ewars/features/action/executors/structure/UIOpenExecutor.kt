package archyonix.ewars.features.action.executors.structure

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("STRUCTURE_UI_OPEN", ClassLibrary.Type.ACTION_EXECUTOR)
class UIOpenExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem) throw RuntimeException("Executor can only be called by a game item")
        val point = player.getTargetBlockPosition(5) ?: return false
        val structure = game.structureManager.findStructure3D(point) ?: return false
        if (structure.getProcess().type != Structure.Process.Type.IDLE) return false
        return structure.team == player.gameTeam()
    }
}