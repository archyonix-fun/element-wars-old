package archyonix.ewars.entities.structure.handler

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.structureManager
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent

class StructureHandler : PublicEventNode("structure-handler") {
    init {
        priority = 0
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            val structure = structureManager.findStructure<Structure>(event.blockPosition) ?: return@addListener
            if (structure.hasBuilding()) return@addListener
            if (structure.team != event.player.gameTeam()) return@addListener
            structure.openUI(event.player)
            event.isCancelled = true
        }
    }
}