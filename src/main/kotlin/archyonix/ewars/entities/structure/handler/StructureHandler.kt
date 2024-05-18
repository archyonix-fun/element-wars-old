package archyonix.ewars.entities.structure.handler

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.structureManager
import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent

class StructureHandler : PublicEventNode("structure-handler") {
    init {
        priority = 1
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            if(event.hand != Player.Hand.MAIN) return@addListener
            if(event.player.itemInMainHand.hasGameItem(GameItem.CONSTRUCTION_HAMMER)) return@addListener
            val structure = structureManager.findStructure<Structure>(event.blockPosition) ?: return@addListener
            if (structure.hasBuilding()) return@addListener
            if (structure.team != event.player.gameTeam()) return@addListener
            structure.openUI(event.player)
            event.isCancelled = true
        }

        addChild(StructureBuildHandler())
    }
}