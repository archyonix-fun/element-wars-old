package archyonix.ewars.features.pvp

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import kotlin.math.cos
import kotlin.math.sin

class Util {
    companion object {
        fun rotate(origin: Point, yaw: Float, point: Point): Point {
            val oX = origin.x()
            val oZ = origin.z()
            val pX = point.x()
            val pZ = point.z()

            val angle = Math.toRadians(yaw + 90.0)
            val qX = oX + cos(angle) * (pX - oX) - sin(angle) * (pZ - oZ)
            val qZ = oZ + sin(angle) * (pX - oX) + cos(angle) * (pZ - oZ)
            return Vec(qX, point.y(), qZ)
        }

        fun showEffect(player: Player, pos: Point, particle: Particle = Particle.CRIT) {
            player.sendPacket(
                ParticlePacket(
                    particle.id(),
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
    }
}