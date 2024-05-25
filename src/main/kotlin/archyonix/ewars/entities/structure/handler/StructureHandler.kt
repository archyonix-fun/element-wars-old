package archyonix.ewars.entities.structure.handler

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.controllers.walls.WallsHandler
import archyonix.ewars.entities.structure.functions.showRegion
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game
import archyonix.ewars.game.events.GameTickEvent
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.tag.Tag

class StructureHandler : PublicEventNode("structure-handler") {
    init {
        priority = -1
        addListener(GameTickEvent::class.java) {
            game.structureManager.updateStructures()
            showStructureSchemaArea()
        }
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            val structure = game.structureManager.findStructure3D(event.blockPosition)
                ?: return@addListener
            if (event.player.gameTeam() != structure.team)
                return@addListener
            val variant = structure.getVariant()
                ?: return@addListener

            variant.ui?.buildUI(event.player, structure)
        }

        addChild(WallsHandler())
    }

    private fun showStructureSchemaArea() {
        game.getPlayers()
            .filter { it.isOnGround }
            .filter { it.itemInMainHand.hasGameItem() }
            .filter { it.itemInMainHand.hasGameItem(GameItem.STRUCTURE_SCHEMA) }
            .forEach {
                val structureId = it.itemInMainHand.getTag(Tag.String("structureId"))
                val structureConfig = game.structureManager.getConfig(structureId)

                val zeroPosition = it.position.withGrid(structureConfig.grid)
                val region2D = Region2D(zeroPosition, structureConfig.area)
                showRegion(region2D, it)
            }
    }
}