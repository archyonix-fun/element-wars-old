package archyonix.ewars.features.action.executors.structure.controller.walls

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.controllers.walls.WallsController
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("STRUCTURE_WALLS_BUILD", ClassLibrary.Type.ACTION_EXECUTOR)
class BuildExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem.Variant) throw RuntimeException("Executor can only be called by a game item")
        if (!player.itemInMainHand.hasCharged()) return false

        val point = player.getTargetBlockPosition(5)
            ?.add(0.0, 1.0, 0.0) ?: return false
        val structure = game.structureManager.findStructure3D(point) ?: return false
        if (structure.team != player.gameTeam()) return false

        val controller = structure.config.getController(WallsController.Config::class.java)
            ?.getController(structure) ?: return false
        val offset = controller.offsetByPoint(point) ?: return false
        if (controller.sampleTypeByOffset(offset) != WallsController.Place.SampleType.NONE) return false
        return controller.modify(offset, WallsController.Place.SampleType.WALL)
    }
}