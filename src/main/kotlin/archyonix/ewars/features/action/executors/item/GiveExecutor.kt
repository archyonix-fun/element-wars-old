package archyonix.ewars.features.action.executors.item

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.message.Message
import archyonix.ewars.entities.player.addItem
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack

@ClassLibrary.RegisteredClass("ITEM_GIVE", ClassLibrary.Type.ACTION_EXECUTOR)
class GiveExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    val item = run {
        val itemId = (properties["id"] ?: "") as String
        return@run GameItem.items().find { it.id == itemId }
            ?: throw RuntimeException("Unknown item: $itemId")
    }
    val variant = run {
        val variantId = (properties["variant"] ?: "") as String
        return@run item.getVariant(variantId)
            ?: throw RuntimeException("Unknown variant: $variantId")
    }
    private val amount = properties.getOrDefault("amount", 1) as Int
    override fun run(player: Player, caller: Any): Boolean {
        if (item.union != null) {
            val savedItem = player.inventory.itemStacks
                .filter { it.hasGameItem(item) }
                .find { it.asItemVariant()?.order!! >= variant.order } != null
            if (savedItem) {
                player.showActionBar(Message.ALREADY_PURCHASED, MessageType.WARNING)
                return false
            } else clearUnionItems(player, item.union)
        }
        player.addItem(variant, amount)
        return true
    }

    private fun clearUnionItems(player: Player, union: String) {
        val inv = player.inventory
        for (slot in 0 until inv.size) {
            if (inv.getItemStack(slot).asGameItem()?.union == union) {
                inv.setItemStack(slot, ItemStack.AIR)
                break
            }
        }
    }
}