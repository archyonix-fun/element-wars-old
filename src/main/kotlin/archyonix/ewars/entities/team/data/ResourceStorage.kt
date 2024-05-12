package archyonix.ewars.entities.team.data

import kotlin.math.max
import kotlin.math.min

class ResourceStorage {
    var NATURAL_CRYSTAL: Int = 1000
        set(value) {
            field = max(0, min(value, 9999))
        }
    var FLAME_CRYSTAL: Int = 1000
        set(value) {
            field = max(0, min(value, 9999))
        }
    var EARTH_CRYSTAL: Int = 1000
        set(value) {
            field = max(0, min(value, 9999))
        }
    var WATER_CRYSTAL: Int = 1000
        set(value) {
            field = max(0, min(value, 9999))
        }
    var ENERGY_LIMIT: Int = 24
        set(value) {
            field = max(0, value)
        }
    var ENERGY_USED: Int = 0
        set(value) {
            field = max(0, min(value, ENERGY_LIMIT))
        }
}