package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.events.api.WarningMessageEvent
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.game.events.GameEvent

class StructureValidateBuildZoneEvent(
    val structureConfig: Structure.Config,
    val airZone: Zone,
    val baseZone: Zone,
) : GameEvent, WarningMessageEvent() {
    var isValid: Boolean = false

    fun validateBaseZone(errorLimit: Int): Boolean {
        val notSolidBlockCount = this.baseZone.notSolidBlockCount(errorLimit)
        return notSolidBlockCount < errorLimit
    }

    fun validateAirZone(errorLimit: Int): Boolean {
        val solidBlockCount = this.airZone.solidBlockCount(errorLimit)
        return solidBlockCount < errorLimit
    }
}