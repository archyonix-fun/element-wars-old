package archyonix.ewars.entities.`$structure`.events

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.game.events.GameEvent
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.event.trait.CancellableEvent

data class StructureShowAreaEvent(
    val structureConfig: Structure.Config,
    val builder: Player,
    val position: Point
) : GameEvent, CancellableEvent {
    override fun isCancelled(): Boolean = isCancelled
    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}