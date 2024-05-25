package archyonix.ewars.features.action.executors.structure

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.ui.UI
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("STRUCTURE_DECONSTRUCT", ClassLibrary.Type.ACTION_EXECUTOR)
class DeconstructExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        val structure = if (caller is Structure) caller
        else if (caller is UI && caller.owner is Structure) caller.owner
        else throw RuntimeException("Executor can only be called by the structure")
        return game.structureManager.deconstructStructure(structure)
    }
}