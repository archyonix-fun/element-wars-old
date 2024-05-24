package archyonix.ewars.features.action.executors.structure

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.structure.events.StructurePrePlaceEvent
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

@ClassLibrary.RegisteredClass("STRUCTURE_PLACE", ClassLibrary.Type.ACTION_EXECUTOR)
class PlaceExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem) throw RuntimeException("Executor can only be called by a game item")
        if (caller != GameItem.STRUCTURE_SCHEMA) return false

        val item = player.itemInMainHand
        val structureId = item.getTag(Tag.String("structureId"))

        val placeEvent = StructurePrePlaceEvent(player, structureId)
        game.handler.call(placeEvent)
        if (placeEvent.isCancelled) return false

        return if (game.structureManager.createStructure(structureId, player)) {
            player.itemInMainHand =
                if (item.amount() > 1) item.withAmount(item.amount() - 1)
                else ItemStack.AIR
            true
        } else false
    }
}