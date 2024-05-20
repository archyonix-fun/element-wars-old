package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player

data class CalculateStructurePositionEvent(
    val structureConfig: Structure.Config,
    val builder: Player
) : GameEvent {
    var resultPosition: Point? = null
}