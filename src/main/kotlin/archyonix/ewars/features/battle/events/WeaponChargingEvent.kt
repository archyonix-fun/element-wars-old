package archyonix.ewars.features.battle.events

import archyonix.ewars.game.events.GameEvent
import net.minestom.server.entity.Player

class WeaponChargingEvent(
    player: Player,
    level: Int
): GameEvent