package archyonix.ewars.features.action.executors.structure.controller.walls

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.controllers.walls.WallsController
import archyonix.ewars.entities.structure.types.StructureOffset
import archyonix.ewars.entities.ui.UI
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("STRUCTURE_WALLS_UI", ClassLibrary.Type.ACTION_EXECUTOR)
class UIOpenExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem) throw RuntimeException("Executor can only be called by a game item")

        val point = player.getTargetBlockPosition(5)
            ?.add(0.0, 1.0, 0.0) ?: return false
        val structure = game.structureManager.findStructure3D(point) ?: return false
        if (structure.team != player.gameTeam()) return false

        val controller = structure.config.getController(WallsController.Config::class.java)
            ?.getController(structure) ?: return false
        val offset = controller.offsetByPoint(point) ?: return false
        when (controller.sampleTypeByOffset(offset)) {
            WallsController.Place.SampleType.WALL -> {
                val sampleId = controller.config.places[offset.name]?.sampleId ?: return false
                val sample = controller.config.samples[sampleId] ?: return false
                val path = if (sample.gateConfig == null) "gate-not-available.yaml" else "gate-available.yaml"
                UI.Constructor("config/ui/walls/$path")
                    .buildUI(player, WallData(controller, offset))
            }

            WallsController.Place.SampleType.GATE ->
                UI.Constructor("config/ui/walls/gate.yaml")
                    .buildUI(player, WallData(controller, offset))

            else -> return false
        }
        return true
    }

    data class WallData(
        val controller: WallsController,
        val offset: StructureOffset.Global2D
    )
}