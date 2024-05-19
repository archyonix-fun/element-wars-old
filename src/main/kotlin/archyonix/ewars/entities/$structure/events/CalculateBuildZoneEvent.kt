package archyonix.ewars.entities.`$structure`.events

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.game.events.GameEvent

data class CalculateBuildZoneEvent(
    val structureConfig: Structure.Config,
    val zeroY: Int,
    val region2D: Region2D
) : GameEvent {
    var resultBaseZone: Zone? = null
    var resultAirZone: Zone? = null
}