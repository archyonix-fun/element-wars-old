package archyonix.ewars.features.action.handler

import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.player.useCoolDown
import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.event.player.PlayerUseItemEvent

class ItemActionsHandler : PublicEventNode("item-actions") {
    init {
        priority = 0
        addListener(PlayerBlockInteractEvent::class.java) { event ->
            if (event.isCancelled) return@addListener
            if (event.hand != Player.Hand.MAIN) return@addListener
            val item = event.player.itemInMainHand.asItemVariant() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            item.executeActions(event.player, mutableMapOf(Pair("RIGHT_CLICK_BLOCK", true)))
        }
        addListener(PlayerUseItemEvent::class.java) { event ->
            if (event.isCancelled) return@addListener
            if (event.hand != Player.Hand.MAIN) return@addListener
            val item = event.player.itemInMainHand.asItemVariant() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            item.executeActions(event.player, mutableMapOf(Pair("RIGHT_CLICK", true)))
        }
        addListener(PlayerHandAnimationEvent::class.java) { event ->
            if (event.isCancelled) return@addListener
            if (event.hand != Player.Hand.MAIN) return@addListener
            val item = event.player.itemInMainHand.asItemVariant() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            val blockPosition = event.player.getTargetBlockPosition(5)

            if (blockPosition != null) item.executeActions(event.player, mutableMapOf(Pair("LEFT_CLICK_BLOCK", true)))
            else item.executeActions(event.player, mutableMapOf(Pair("LEFT_CLICK", true)))
        }
        addListener(PlayerSwapItemEvent::class.java) { event ->
            if (event.isCancelled) return@addListener
            val item = event.player.itemInMainHand.asItemVariant() ?: return@addListener
            if (!event.player.useCoolDown()) return@addListener
            item.executeActions(event.player, mutableMapOf(Pair("SWAP", true)))
        }
    }
}