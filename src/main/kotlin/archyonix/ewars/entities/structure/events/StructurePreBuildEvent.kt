package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent
import net.minestom.server.entity.Player
import net.minestom.server.event.trait.CancellableEvent

class StructurePreBuildEvent(
    val structure: Structure,
    val builder: Player,
    var buildPoints: Int
) : GameEvent, CancellableEvent {
    override fun isCancelled(): Boolean = isCancelled
    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}