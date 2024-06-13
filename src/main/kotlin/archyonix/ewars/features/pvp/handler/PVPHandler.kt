package archyonix.ewars.features.pvp.handler

import archyonix.ewars.entities.player.setFocus
import archyonix.ewars.features.pvp.FocusController
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
            it.player.setFocus(FocusController.Focus.NONE)
        }
        addListener(PlayerMoveEvent::class.java) {
            if (it.player.position.asVec() == it.newPosition.asVec()) return@addListener
            if (!it.player.isSneaking) return@addListener
            val vector = it.newPosition.asVec().sub(it.player.position.asVec()).normalize()
            it.player.velocity = vector.add(0.0, 0.1, 0.0).mul(25.0)
            it.player.isSneaking = false
        }
        addListener(PlayerStartSneakingEvent::class.java) {
            game.pvpManager.resetCharge(it.player)
            it.player.setFocus(FocusController.Focus.NONE)
        }
    }
}