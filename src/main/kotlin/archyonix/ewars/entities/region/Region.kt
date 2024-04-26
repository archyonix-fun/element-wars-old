package archyonix.ewars.entities.region

import net.minestom.server.coordinate.Point

class Region(
    private val startX: Int,
    private val startZ: Int,
    private val endX: Int,
    private val endZ: Int,
) {
    constructor(pos: Point, radius: Int) : this(
        pos.blockX() - radius,
        pos.blockZ() - radius,
        pos.blockX() + radius,
        pos.blockZ() + radius,
    )

    fun contains(region: Region): Boolean {
        if (region.startX > endX) return false
        if (region.endX < startX) return false

        if (region.startZ > endZ) return false
        return region.endZ >= startZ
    }
}