package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.`$structure`.events.StructurePrePlaceEvent
import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

@ClassLibrary.RegisteredClass("STRUCTURE_PLACE", ClassLibrary.Type.EXECUTOR)
class StructurePlaceExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player): Boolean {
        val itemStack = player.itemInMainHand
        val item = itemStack.asGameItem() ?: return false
        if (item != GameItem.STRUCTURE_SCHEMA) return false

        val structureId = itemStack.getTag(Tag.String("structureId"))

        val placeEvent = StructurePrePlaceEvent(player, structureId)
        game.handler.call(placeEvent)
        if (placeEvent.isCancelled) return false

        return if (game.structureManager.createStructure(structureId, player)) {
            player.itemInMainHand =
                if (itemStack.amount() > 1) itemStack.withAmount(itemStack.amount() - 1)
                else ItemStack.AIR
            true
        } else false
    }
}