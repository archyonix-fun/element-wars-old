package archyonix.ewars.shared.libs

import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos

data class CellPosition(
    val x: Int,
    val z: Int,
) {
    constructor(pos: Point): this(pos.blockX(), pos.blockZ())

    fun toPos(): Pos {
        var y = 360
        while (!game.instance.getBlock(x, y, z).isSolid) {
            if(y < 0) break
            else y--
        }
        return Pos(x.toDouble(), y.toDouble(), z.toDouble())
    }
}