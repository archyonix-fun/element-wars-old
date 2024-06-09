package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent

class StructureProcessFinishEvent(
    val structure: Structure,
    val processType: Structure.Process.Type
) : GameEvent