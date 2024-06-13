package archyonix.ewars.features.pvp.handler

import archyonix.ewars.entities.player.setFocus
import archyonix.ewars.features.pvp.PVPManager
import archyonix.ewars.features.pvp.task.FocusTask
import archyonix.ewars.game
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartSneakingEvent

class PVPHandler : PublicEventNode("PVPHandler") {
    init {
        FocusTask().run()

        addListener(PlayerMoveEvent::class.java) {
            if (it.player.position.asVec() == it.newPosition.asVec()) return@addListener
            game.pvpManager.resetCharge(it.player)
            it.player.setFocus(PVPManager.Focus.NONE)
        }
        addListener(PlayerStartSneakingEvent::class.java) {
            game.pvpManager.resetCharge(it.player)
            it.player.setFocus(PVPManager.Focus.NONE)
        }
    }
}