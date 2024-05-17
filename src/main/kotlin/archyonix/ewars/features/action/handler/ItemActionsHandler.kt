package archyonix.ewars.features.action.handler

import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.entities.player.useCoolDown
import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.event.player.PlayerUseItemEvent

class ItemActionsHandler : PublicEventNode("item-actions") {
    init {
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            if (event.hand != Player.Hand.MAIN) return@addListener
            val item = event.player.itemInMainHand.asGameItem() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            item.executeAction(event.player, "RIGHT_CLICK_BLOCK")
        }
        addListener(PlayerUseItemEvent::class.java) { event ->
            if (event.hand != Player.Hand.MAIN) return@addListener
            val item = event.player.itemInMainHand.asGameItem() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            item.executeAction(event.player, "RIGHT_CLICK")
        }
        addListener(PlayerHandAnimationEvent::class.java) { event ->
            if (event.hand != Player.Hand.MAIN) return@addListener
            val item = event.player.itemInMainHand.asGameItem() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            val blockPosition = event.player.getTargetBlockPosition(5)

            if (blockPosition != null) item.executeAction(event.player, "LEFT_CLICK_BLOCK")
            else item.executeAction(event.player, "LEFT_CLICK")
        }
        addListener(PlayerSwapItemEvent::class.java) { event ->
            val item = event.player.itemInMainHand.asGameItem() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            item.executeAction(event.player, "SWAP")
        }
    }
}