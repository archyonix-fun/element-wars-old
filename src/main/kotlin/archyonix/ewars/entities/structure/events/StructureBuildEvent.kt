package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent

class StructureBuildEvent(
    val structure: Structure,
    val buildPoints: Int
): GameEvent