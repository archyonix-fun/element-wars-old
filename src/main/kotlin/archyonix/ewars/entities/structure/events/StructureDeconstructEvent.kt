package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent
import net.minestom.server.event.trait.CancellableEvent

class StructureDeconstructEvent(
    val structure: Structure
) : GameEvent, CancellableEvent {
    override fun isCancelled(): Boolean = isCancelled

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}