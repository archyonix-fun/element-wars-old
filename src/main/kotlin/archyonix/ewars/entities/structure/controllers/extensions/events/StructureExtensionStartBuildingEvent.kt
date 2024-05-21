package archyonix.ewars.entities.structure.controllers.extensions.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.events.GameEvent

class StructureExtensionStartBuildingEvent(
    val structure: Structure,
    var extension: Structure.Extension
): GameEvent