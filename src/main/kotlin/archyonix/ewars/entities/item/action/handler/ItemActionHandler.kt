package archyonix.ewars.entities.item.action.handler

import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.game
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.*
import net.minestom.server.instance.block.Block

class ItemActionHandler : PublicEventNode("item-actions") {
    init {
        priority = 1
        addListener(PlayerBlockPlaceEvent::class.java) { event ->
            event.isCancelled = true
        }
        addListener(PlayerBlockBreakEvent::class.java) { event ->
            event.isCancelled = true
        }
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            val itemStack = event.player.itemInMainHand
            if (!itemStack.hasGameItem()) return@addListener
            itemStack.getGameItem()?.executeAction(event.player, "RIGHT_CLICK_BLOCK")
            event.isCancelled = true
        }
        addListener(PlayerHandAnimationEvent::class.java) { event ->
            val itemStack = event.player.itemInMainHand
            if (!itemStack.hasGameItem()) return@addListener
            val player = event.player
            val blockPosition = player.getTargetBlockPosition(5) ?: return@addListener
            val block = game.instance.getBlock(blockPosition)
            if (block == Block.AIR) return@addListener

            itemStack.getGameItem()?.executeAction(event.player, "LEFT_CLICK_BLOCK")
        }
        addListener(PlayerUseItemEvent::class.java) { event ->
            val itemStack = event.itemStack
            if (!itemStack.hasGameItem()) return@addListener
            itemStack.getGameItem()?.executeAction(event.player, "RIGHT_CLICK")
            event.isCancelled = true
        }

        addListener(PlayerSwapItemEvent::class.java) { event ->
            event.isCancelled = true
        }
    }
}