package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent
import net.minestom.server.entity.Player
import net.minestom.server.utils.Direction

data class StructureCalculateDirectionEvent(
    val structureConfig: Structure.Config,
    val builder: Player
) : GameEvent {
    var resultDirection: Direction? = null
}