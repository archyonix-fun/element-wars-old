package archyonix.ewars.features.mine_water.events

import archyonix.ewars.game.events.GameEvent
import net.minestom.server.entity.Player

class SuccessFishingEvent(
    val player: Player
) : GameEvent