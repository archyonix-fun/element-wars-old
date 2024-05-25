package archyonix.ewars.features.action.executors.structure.controller.walls

import archyonix.ewars.entities.structure.controllers.walls.WallsController
import archyonix.ewars.entities.ui.UI
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("STRUCTURE_WALLS_MODIFY", ClassLibrary.Type.ACTION_EXECUTOR)
class ModifyExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val sampleType = properties["sampleType"] ?: throw RuntimeException("Wall sampleType is null")
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is UI) throw RuntimeException("Executor can only be called by a ui")
        if (caller.owner !is WallsController.WallData) throw RuntimeException("Executor can only be called by a wall data")
        return caller.owner.controller.modify(
            caller.owner.offset,
            WallsController.Place.SampleType.valueOf(sampleType as String)
        )
    }
}