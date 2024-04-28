package archyonix.ewars.entities.item.action.handler

import archyonix.ewars.actionManager
import archyonix.ewars.entities.item.action.GameTrigger
import archyonix.ewars.entities.item.functions.hasGameItem
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerUseItemEvent

class ItemActionHandler : PublicEventNode("item-actions") {
    init {
        addListener(PlayerBlockPlaceEvent::class.java) { event ->
            event.isCancelled = true
        }
        addListener(PlayerBlockBreakEvent::class.java) { event ->
            event.isCancelled = true
        }
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            val itemStack = event.player.itemInMainHand
            if (!itemStack.hasGameItem()) return@addListener

            actionManager.call(GameTrigger.RIGHT_CLICK_BLOCK, event.player)
            event.isCancelled = true
        }
        addListener(PlayerUseItemEvent::class.java) { event ->
            val itemStack = event.itemStack
            if (!itemStack.hasGameItem()) return@addListener

            actionManager.call(GameTrigger.RIGHT_CLICK, event.player)
            event.isCancelled = true
        }
    }
}