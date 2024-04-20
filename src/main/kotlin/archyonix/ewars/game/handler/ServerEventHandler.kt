package archyonix.ewars.game.handler

import archyonix.ewars.game.events.PlayerJoinTheGameEvent
import archyonix.ewars.game.functions.findPlayerByUsername
import archyonix.ewars.shared.lib.events
import archyonix.ewars.shared.lib.game
import archyonix.ewars.shared.lib.gameArgs
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.getType
import archyonix.ewars.entities.player.setType
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerPacketOutEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket

class ServerEventHandler() : PublicEventNode("server") {
    init {
        addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            event.spawningInstance = game!!.instance

            val player = event.player
            val isAccess = gameArgs!!.PLAYER_LIST.contains(player.username)
            player.setType(if (isAccess) PlayerType.PLAYER else PlayerType.SPECTATOR)

            player.respawnPoint = game!!.map.WORLD_SPAWN
        }

        addListener(PlayerSpawnEvent::class.java) { event ->
            val player = event.player

            events!!.call(PlayerJoinTheGameEvent(player, player.getType()))
        }

        addListener(PlayerPacketOutEvent::class.java) { event ->
            val packet = event.packet

            if (packet is PlayerInfoUpdatePacket) {
                if(!packet.actions().contains(PlayerInfoUpdatePacket.Action.ADD_PLAYER)) return@addListener
                val addPlayer = findPlayerByUsername(packet.entries()[0].username)
                event.isCancelled = addPlayer?.getType() == PlayerType.SPECTATOR
            }
        }

        addChild(GameEventHandler())
    }
}