package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.game.events.GameEvent

data class PrepareBuildZoneEvent(
    val structureConfig: Structure.Config,
    val airZone: Zone,
    val baseZone: Zone,
): GameEvent