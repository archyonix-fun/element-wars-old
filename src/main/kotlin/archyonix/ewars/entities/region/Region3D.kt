package archyonix.ewars.entities.region

import archyonix.ewars.entities.region.api.Region
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.shared.libs.asPos
import archyonix.ewars.shared.libs.withDirection
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.max
import kotlin.math.min

class Region3D(
    private val startX: Int,
    private val startY: Int,
    private val startZ: Int,
    private val endX: Int,
    private val endY: Int,
    private val endZ: Int,
) : Region {
    constructor(startPoint: Point, endPoint: Point) : this(
        startPoint.blockX(), startPoint.blockY(), startPoint.blockZ(),
        endPoint.blockX(), endPoint.blockY(), endPoint.blockZ()
    )

    constructor(origin: Point, area: Structure.Config.Area) : this(
        startX = origin.blockX() - area.radius,
        startY = origin.blockY(),
        startZ = origin.blockZ() - area.radius,
        endX = origin.blockX() + area.radius + if (area.isEven) -1 else 0,
        endY = origin.blockY() + area.height - 1,
        endZ = origin.blockZ() + area.radius + if (area.isEven) -1 else 0,
    )

    constructor(config: ConfigurationSection) : this(
        config.getString("startPoint")?.asPos()
            ?: throw RuntimeException("Region startPoint not found"),
        config.getString("endPoint")?.asPos()
            ?: throw RuntimeException("Region endPoint not found"),
    )

    override fun getWidth(): Int = endX - startX + 1
    override fun getHeight(): Int = endY - startY + 1
    override fun getLength(): Int = endZ - startZ + 1

    override fun contains(other: Region): Boolean {
        other as Region3D
        if (other.startX > endX) return false
        if (other.endX < startX) return false

        if (other.startY > endY) return false
        if (other.endY < startY) return false

        if (other.startZ > endZ) return false
        return other.endZ >= startZ
    }

    override fun contains(point: Point): Boolean {
        if (point.blockX() > endX) return false
        if (point.blockX() < startX) return false

        if (point.blockY() > endY) return false
        if (point.blockY() < startY) return false

        if (point.blockZ() > endZ) return false
        return point.blockZ() >= startZ
    }

    override fun merge(other: Region): Region {
        other as Region3D
        val startX = if (this.startX < other.startX) this.startX else other.startX
        val startY = if (this.startY < other.startY) this.startY else other.startY
        val startZ = if (this.startZ < other.startZ) this.startZ else other.startZ

        val endX = if (this.endX > other.endX) this.endX else other.endX
        val endY = if (this.endY > other.endY) this.endY else other.endY
        val endZ = if (this.endZ > other.endZ) this.endZ else other.endZ

        return Region3D(startX, startY, startZ, endX, endY, endZ)
    }

    override fun normalize(): Region {
        val startX = min(this.startX, this.endX)
        val startY = min(this.startY, this.endY)
        val startZ = min(this.startZ, this.endZ)

        val endX = max(this.startX, this.endX)
        val endY = max(this.startY, this.endY)
        val endZ = max(this.startZ, this.endZ)

        return Region3D(startX, startY, startZ, endX, endY, endZ)
    }

    override fun offset(absolutePoint: Point): Region {
        return Region3D(
            startPoint().add(absolutePoint),
            endPoint().add(absolutePoint)
        )
    }

    override fun rotateY(direction: Direction, planeSize: Int): Region {
        return Region3D(
            startPoint().withDirection(direction, planeSize),
            endPoint().withDirection(direction, planeSize),
        ).normalize()
    }

    override fun startPoint(): Point = Vec(
        startX.toDouble(), startY.toDouble(), startZ.toDouble()
    )

    override fun endPoint(): Point = Vec(
        endX.toDouble(), endY.toDouble(), endZ.toDouble()
    )
}