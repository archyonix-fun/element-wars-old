package archyonix.ewars.entities.structure.functions

import net.minestom.server.coordinate.Point

fun Point.withWallGrid(): Point {
    val offsetX = blockX() % 12
    val offsetZ = blockZ() % 12

    var result = this
    result = if(offsetX > 5) result.add(12.0 - offsetX, .0, .0)
    else result.sub(offsetX.toDouble(), .0, .0)
    result = if(offsetZ > 5) result.add(.0,.0,12.0 - offsetZ)
    else result.sub( .0, .0, offsetZ.toDouble())

    return result
}