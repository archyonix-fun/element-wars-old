package archyonix.ewars.features.battle.events

import archyonix.ewars.game.events.GameEvent
import net.minestom.server.entity.Player

class WeaponChargedAttackEvent(
    player: Player,
    level: Int
): GameEvent