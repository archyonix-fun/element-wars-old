package archyonix.ewars.entities.region

import archyonix.ewars.entities.structure.Structure
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Region3D(
    val startX: Int,
    val startY: Int,
    val startZ: Int,
    val endX: Int,
    val endY: Int,
    val endZ: Int,
) {
    val width = endX - startX
    val height = endY - startY
    val length = endZ - startZ

    companion object {
        fun fromSizeArea(pos: Point, area: Structure.Config.Area): Region3D {
            val radius = floor(area.size / 2.0).toInt()
            val isEven = area.size % 2 == 0
            return Region3D(pos, radius, area.height, isEven)
        }
    }

    constructor(pos: Point, radius: Int, height: Int, isEven: Boolean = false) : this(
        pos.blockX() - radius,
        pos.blockY(),
        pos.blockZ() - radius,
        pos.blockX() + radius + if (isEven) -1 else 0,
        pos.blockY() + height - 1,
        pos.blockZ() + radius + if (isEven) -1 else 0,
    )

    constructor(startPoint: Point, endPoint: Point) : this(
        startPoint.blockX(), startPoint.blockY(), startPoint.blockZ(),
        endPoint.blockX(), endPoint.blockY(), endPoint.blockZ()
    )

    fun contains(region: Region3D): Boolean {
        if (region.startX > endX) return false
        if (region.endX < startX) return false

        if (region.startY > endY) return false
        if (region.endY < startY) return false

        if (region.startZ > endZ) return false
        return region.endZ >= startZ
    }

    fun contains(pos: Point): Boolean {
        if (pos.blockX() > endX) return false
        if (pos.blockX() < startX) return false

        if (pos.blockY() > endY) return false
        if (pos.blockY() < startY) return false

        if (pos.blockZ() > endZ) return false
        return pos.blockZ() >= startZ
    }

    fun merge(region: Region3D): Region3D {
        val minX = if (startX < region.startX) startX else region.startX
        val minY = if (startY < region.startY) startY else region.startY
        val minZ = if (startZ < region.startZ) startZ else region.startZ

        val maxX = if (endX > region.endX) endX else region.endX
        val maxY = if (endY > region.endY) endY else region.endY
        val maxZ = if (endZ > region.endZ) endZ else region.endZ

        return Region3D(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun findCenter(): Point {
        val centerX = endX - (width / 2.0)
        val centerY = endY - (height / 2.0)
        val centerZ = endZ - (length / 2.0)

        return Pos(centerX, centerY, centerZ)
    }

    fun normalize(): Region3D {
        val minX = min(startX, endX)
        val minY = min(startY, endY)
        val minZ = min(startZ, endZ)

        val maxX = max(startX, endX)
        val maxY = max(startY, endY)
        val maxZ = max(startZ, endZ)

        return Region3D(minX, minY, maxZ, maxX, maxY, maxZ)
    }

    fun minPoint(): Point = Pos(startX.toDouble(), startY.toDouble(), startZ.toDouble())
    fun maxPoint(): Point = Pos(endX.toDouble(), endY.toDouble(), endZ.toDouble())
}