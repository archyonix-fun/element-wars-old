package archyonix.ewars.entities.structure.handler

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.task.StructureUpdateTask
import archyonix.ewars.game
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent

class StructureHandler : PublicEventNode("structure-handler") {
    init {
        game.schedule.scheduleNextTick(StructureUpdateTask())

        addListener(PlayerBlockInteractEvent::class.java) { event ->
            val structure = game.structureManager.findStructure3D(event.blockPosition)
                ?: return@addListener
            if (event.player.gameTeam() != structure.team)
                return@addListener
            val variant = structure.getVariant()
                ?: return@addListener

            variant.ui?.buildUI(event.player)
        }
    }
}