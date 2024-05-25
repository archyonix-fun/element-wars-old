package archyonix.ewars.entities.structure.controllers.walls

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.game
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent

class WallsHandler : PublicEventNode("walls handler") {
    init {
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            val player = event.player
            val structure = game.structureManager.findStructure3D(event.blockPosition)
                ?: return@addListener
            if (player.gameTeam() != structure.team)
                return@addListener

            val point = event.blockPosition
            val controller = structure.config.getController(WallsController.Config::class.java)
                ?.getController(structure) ?: return@addListener
            val offset = controller.offsetByPoint(point) ?: return@addListener
            if(controller.openUI(offset, player)) event.isCancelled = true
        }
    }
}