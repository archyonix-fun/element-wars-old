package archyonix.ewars.game.handler

import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerSwapItemEvent

class CancelEventHandler : PublicEventNode("cancel-handler") {
    init {
        priority = 999
        addListener(PlayerBlockBreakEvent::class.java) { it.isCancelled = true }
        addListener(PlayerBlockPlaceEvent::class.java) { it.isCancelled = true }
        addListener(PlayerSwapItemEvent::class.java) { it.isCancelled = true }
    }
}