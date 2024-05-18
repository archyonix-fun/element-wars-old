package archyonix.ewars.game.handler

import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.getRole
import archyonix.ewars.entities.player.setRole
import archyonix.ewars.events
import archyonix.ewars.game
import archyonix.ewars.game.events.PlayerJoinTheGameEvent
import archyonix.ewars.game.functions.findPlayerByUsername
import archyonix.ewars.gameArgs
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerPacketOutEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket

class ServerEventHandler() : PublicEventNode("server") {
    init {
        addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            event.spawningInstance = game.instance

            val player = event.player
            val isAccess = gameArgs.PLAYER_LIST.contains(player.username)
            if (game.getPlayers().size >= game.maxPlayers && isAccess) player.kick("Мест нет")
            player.setRole(if (isAccess) PlayerType.PLAYER else PlayerType.SPECTATOR)

            player.respawnPoint = game.map.WORLD_SPAWN
        }

        addListener(PlayerSpawnEvent::class.java) { event ->
            val player = event.player

            events.call(PlayerJoinTheGameEvent(player, player.getRole()))
        }

        addListener(ItemDropEvent::class.java) { event ->
            event.isCancelled = true
        }

        addListener(PlayerPacketOutEvent::class.java) { event ->
            val packet = event.packet

            if (packet is PlayerInfoUpdatePacket) {
                if (packet.actions().contains(PlayerInfoUpdatePacket.Action.ADD_PLAYER)) {
                    val addPlayer = findPlayerByUsername(packet.entries()[0].username)
                    event.isCancelled = addPlayer?.getRole() == PlayerType.SPECTATOR
                }
            }
        }

        addChild(GameEventHandler())
    }
}