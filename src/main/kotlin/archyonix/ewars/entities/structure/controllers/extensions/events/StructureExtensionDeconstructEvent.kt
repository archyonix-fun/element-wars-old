package archyonix.ewars.entities.structure.controllers.extensions.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent
import net.minestom.server.event.trait.CancellableEvent

class StructureExtensionDeconstructEvent(
    val structure: Structure,
    val extension: Structure.Extension
) : GameEvent, CancellableEvent {
    private var isCancelled: Boolean = false
    override fun isCancelled(): Boolean = isCancelled

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}