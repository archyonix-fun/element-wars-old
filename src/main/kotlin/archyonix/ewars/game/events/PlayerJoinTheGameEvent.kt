package archyonix.ewars.game.events

import archyonix.ewars.entities.player.PlayerType
import net.minestom.server.entity.Player

class PlayerJoinTheGameEvent(
    val player: Player,
    val playerType: PlayerType,
): GameEvent