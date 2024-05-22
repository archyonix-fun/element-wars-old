package archyonix.ewars.entities.structure.functions

import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.region.Region3D
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import kotlin.math.floor

fun Point.withGrid(size: Int = 12): Point {
    if (size < 2) return this
    val offsetX = blockX() % size
    val offsetZ = blockZ() % size
    val first = floor(size / 2.0) - 1

    var result = this
    result = if (offsetX > first) result.add(size.toDouble() - offsetX, .0, .0)
    else result.sub(offsetX.toDouble(), .0, .0)
    result = if (offsetZ > first) result.add(.0, .0, size.toDouble() - offsetZ)
    else result.sub(.0, .0, offsetZ.toDouble())

    return result
}

fun showRegion(region2D: Region2D, player: Player) {
    val minX = region2D.startPoint().blockX()
    val maxX = region2D.endPoint().blockX()

    val minZ = region2D.startPoint().blockZ()
    val maxZ = region2D.endPoint().blockZ()

    for (x in minX..maxX) {
        for (z in minZ..maxZ) {
            val pos = Pos(x + .5, player.position.blockY().toDouble(), z + .5)
            when {
                x == minX -> showEffect(player, pos)
                x == maxX -> showEffect(player, pos)
                z == minZ -> showEffect(player, pos)
                z == maxZ -> showEffect(player, pos)
            }
        }
    }
}

fun showRegion(region3D: Region3D, player: Player) {
    val minX = region3D.startPoint().blockX()
    val maxX = region3D.endPoint().blockX()

    val minZ = region3D.startPoint().blockZ()
    val maxZ = region3D.endPoint().blockZ()

    for (x in minX..maxX) {
        for (z in minZ..maxZ) {
            val pos = Pos(x + .5, region3D.startPoint().blockY().toDouble(), z + .5)
            when {
                x == minX -> showEffect(player, pos)
                x == maxX -> showEffect(player, pos)
                z == minZ -> showEffect(player, pos)
                z == maxZ -> showEffect(player, pos)
            }
        }
    }
}

fun showEffect(player: Player, pos: Point) {
    player.sendPacket(
        ParticlePacket(
            Particle.FLAME.id(),
            false,
            pos.x(),
            pos.y(),
            pos.z(),
            0f,
            0f,
            0f,
            0f,
            1,
            ByteArray(0)
        )
    )
}