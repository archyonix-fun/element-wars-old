package archyonix.ewars.entities.region.api

import net.minestom.server.coordinate.Point
import net.minestom.server.utils.Direction

interface Region {
    fun getWidth(): Int
    fun getHeight(): Int
    fun getLength(): Int
    fun contains(other: Region): Boolean
    fun contains(point: Point): Boolean
    fun merge(other: Region): Region
    fun normalize(): Region
    fun offset(absolutePoint: Point): Region
    fun rotateY(direction: Direction, planeSize: Int): Region
    fun startPoint(): Point
    fun endPoint(): Point
}