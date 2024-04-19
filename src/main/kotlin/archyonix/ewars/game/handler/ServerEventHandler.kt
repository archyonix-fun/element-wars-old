package archyonix.ewars.game.handler

import archyonix.ewars.shared.lib.game
import archyonix.ewars.shared.lib.gameArgs
import archyonix.ewars.shared.lib.shared.libs.PlayerType
import archyonix.ewars.shared.lib.shared.libs.setType
import net.minestom.server.event.GameEventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent

class ServerEventHandler() : GameEventNode("server") {
    init {
        addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            event.spawningInstance = game!!.instance

            val player = event.player
            val isAccess = gameArgs!!.PLAYER_LIST.contains(player.username)
            player.setType(if (isAccess) PlayerType.PLAYER else PlayerType.SPECTATOR)

            player.respawnPoint = game!!.map.WORLD_SPAWN
        }
    }
}