package archyonix.ewars.entities.`$structure`.events

import archyonix.ewars.game.events.GameEvent
import net.minestom.server.entity.Player
import net.minestom.server.event.trait.CancellableEvent

class StructurePrePlaceEvent(
    val player: Player,
    val structureId: String
) : GameEvent, CancellableEvent {
    override fun isCancelled(): Boolean = isCancelled

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}