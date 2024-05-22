package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.controllers.walls.WallsController
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("WALLS_BUILD", ClassLibrary.Type.EXECUTOR)
class WallsBuildExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player): Boolean {
        if (!player.itemInMainHand.hasCharged()) return false

        val point = player.getTargetBlockPosition(5)
            ?.add(0.0, 1.0, 0.0) ?: return false
        val structure = game.structureManager.findStructure3D(point) ?: return false
        if (structure.team != player.gameTeam()) return false

        val controller = structure.config.getController(WallsController.Config::class.java)
            ?.getController(structure) ?: return false
        return true
//        val offset = controller.getOffsetByPoint(point) ?: return false
//        if (controller.getTypeFromOffset(offset) != Walls.Config.WallData.Type.NOTHING) return false
//        return controller.modify(offset, Walls.Config.WallData.Type.WALL)
    }
}