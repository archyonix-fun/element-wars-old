package archyonix.ewars.entities.`$structure`.events

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.entities.`$structure`.events.api.ErrorMessageEvent
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.game.events.GameEvent
import net.kyori.adventure.text.Component

class ValidateBuildZoneEvent(
    val structureConfig: Structure.Config,
    val airZone: Zone,
    val baseZone: Zone,
) : GameEvent, ErrorMessageEvent {
    var isValid: Boolean = false
    override var errorMessage: Component? = null

    fun validateBaseZone(errorLimit: Int): Boolean {
        val notSolidBlockCount = this.baseZone.notSolidBlockCount(errorLimit)
        return notSolidBlockCount < errorLimit
    }

    fun validateAirZone(errorLimit: Int): Boolean {
        val solidBlockCount = this.airZone.solidBlockCount(errorLimit)
        return solidBlockCount < errorLimit
    }
}