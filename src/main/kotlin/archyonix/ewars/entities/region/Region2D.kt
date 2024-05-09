package archyonix.ewars.entities.region

import net.minestom.server.coordinate.Point

class Region2D(
    val startX: Int,
    val startZ: Int,
    val endX: Int,
    val endZ: Int,
) {
    constructor(pos: Point, radius: Int, isEven: Boolean = false) : this(
        pos.blockX() - radius,
        pos.blockZ() - radius,
        pos.blockX() + radius + if(isEven) -1 else 0,
        pos.blockZ() + radius + if(isEven) -1 else 0,
    )

    fun contains(region: Region2D): Boolean {
        if (region.startX > endX) return false
        if (region.endX < startX) return false

        if (region.startZ > endZ) return false
        return region.endZ >= startZ
    }

    fun contains(pos: Point): Boolean {
        if (pos.blockX() > endX) return false
        if (pos.blockX() < startX) return false

        if (pos.blockZ() > endZ) return false
        return pos.blockZ() >= startZ
    }

    fun merge(region: Region2D): Region2D {
        val minX = if(startX < region.startX) startX else region.startX
        val minZ = if(startZ < region.startZ) startZ else region.startZ

        val maxX = if(endX > region.endX) endX else region.endX
        val maxZ = if(endZ > region.endZ) endZ else region.endZ

        return Region2D(minX, minZ, maxX, maxZ)
    }
}