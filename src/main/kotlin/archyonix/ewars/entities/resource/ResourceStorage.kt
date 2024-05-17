package archyonix.ewars.entities.resource

import kotlin.math.max
import kotlin.math.min

class ResourceStorage {
    var NATURAL_CRYSTAL: Int = 9999
        set(value) {
            field = max(0, min(value, 9999))
        }
    var FLAME_CRYSTAL: Int = 9999
        set(value) {
            field = max(0, min(value, 9999))
        }
    var EARTH_CRYSTAL: Int = 9999
        set(value) {
            field = max(0, min(value, 9999))
        }
    var WATER_CRYSTAL: Int = 9999
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

    fun add(resource: Resource, count: Int) {
        when (resource) {
            Resource.NATURAL_CRYSTAL -> NATURAL_CRYSTAL += count
            Resource.EARTH_CRYSTAL -> EARTH_CRYSTAL += count
            Resource.FLAME_CRYSTAL -> FLAME_CRYSTAL += count
            Resource.WATER_CRYSTAL -> WATER_CRYSTAL += count
        }
    }
}