package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent

class StructureExtensionStartBuildingEvent(
    val structure: Structure,
    var extension: Structure.Extension
): GameEvent