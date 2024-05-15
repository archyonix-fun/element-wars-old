package archyonix.ewars.entities.resource.events

import archyonix.ewars.game.events.GameEvent
import net.minestom.server.entity.Player

class SuccessFishingEvent(
    val player: Player
) : GameEvent