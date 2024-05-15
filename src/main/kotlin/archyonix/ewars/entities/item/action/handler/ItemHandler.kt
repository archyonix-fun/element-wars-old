package archyonix.ewars.entities.item.action.handler

import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.item.resetCharge
import archyonix.ewars.entities.player.updateRecharge
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.item.ItemStack

class ItemHandler: PublicEventNode("item-handler") {
    init {
        priority = 2
        addListener(PlayerChangeHeldSlotEvent::class.java) { event ->
            val player = event.player
            val itemStack = player.inventory.getItemStack(event.slot.toInt())
            if(itemStack.hasGameItem()) {
                itemStack.resetCharge()
                player.updateRecharge()
            } else player.updateRecharge()
        }

        addListener(PlayerHandAnimationEvent::class.java) { event ->
            val itemStack = event.player.itemInMainHand
            if(itemStack == ItemStack.AIR) return@addListener
            if(!itemStack.hasGameItem()) return@addListener
            itemStack.resetCharge()
        }

        addListener(PlayerUseItemEvent::class.java) { event ->
            val itemStack = event.itemStack
            if(itemStack == ItemStack.AIR) return@addListener
            if(!itemStack.hasGameItem()) return@addListener
            itemStack.resetCharge()
        }
    }
}