package archyonix.ewars.entities.`$structure`.events

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.game.events.GameEvent

class StructurePlaceEvent(
    val structure: Structure,
    var resultVariant: Structure.Config.Variant
): GameEvent