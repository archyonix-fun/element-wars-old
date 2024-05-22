package archyonix.ewars.entities.structure.controllers.extensions.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.extensions.ExtensionsController
import archyonix.ewars.game.events.GameEvent

class StructureExtensionConstructEvent(
    val structure: Structure,
    var extension: ExtensionsController.Extension
): GameEvent