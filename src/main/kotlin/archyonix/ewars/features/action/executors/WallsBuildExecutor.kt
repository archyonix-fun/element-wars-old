package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.controllers.WallController
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
        if (!structure.config.hasController("WALLS")) return false
        if (structure.team != player.gameTeam()) return false

        val controller = structure.config.getController<WallController>("WALLS")
            ?.getStructureController(structure) ?: return false
        val offset = controller.getOffsetByPoint(point) ?: return false
        if (controller.getTypeFromOffset(offset) != WallController.Config.WallData.Type.NOTHING) return false
        return controller.modify(offset, WallController.Config.WallData.Type.WALL)
    }
}