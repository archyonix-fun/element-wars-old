package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.enums.ItemVariant
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.message.Message
import archyonix.ewars.entities.player.addItem
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.action.Action
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack

@Action.RegisteredExecutor("GIVE_ITEM")
class GiveItemExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private var item: GameItem
    private var variant: GameItem.Variant
    private val amount = properties.getOrDefault("amount", 1) as Int
    init {
        val itemId = (properties["id"] ?: "") as String
        this.item = GameItem.items().find { it.id == itemId }
            ?: throw RuntimeException("Unknown item: $itemId")

        val variant = (properties["variant"] ?: "") as String
        this.variant = item.getVariant(variant)
            ?: throw RuntimeException("Unknown variant: $itemId")
    }
    override fun run(player: Player): Boolean {
        if(item.union != null) {
            val savedItem = player.inventory.itemStacks
                .filter { it.hasGameItem(item) }
                .find { it.asItemVariant()?.order!! >= variant.order } != null
            if (savedItem) {
                player.showActionBar(Message.ALREADY_PURCHASED, MessageType.WARNING)
                return false
            } else clearByUnion(player, item.union!!)
        }
        player.addItem(variant, amount)
        return true
    }

    private fun clearByUnion(player: Player, union: String) {
        val inv = player.inventory
        for (slot in 0 until inv.size) {
            if (inv.getItemStack(slot).asGameItem()?.union == union) {
                inv.setItemStack(slot, ItemStack.AIR)
                break
            }
        }
    }
}