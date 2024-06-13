package archyonix.ewars.features.pvp.enums

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.particle.ParticleCreator
import java.awt.Color

enum class Particles(
    private val color: Color
) {
    DEFAULT(Color.WHITE),
    CHARGED(Color.BLUE);

    fun buildPacket(pos: Point, offset: Vec = Vec.ZERO, count: Int = 1): ParticlePacket {
        return ParticleCreator.createParticlePacket(
            Particle.DUST,
            true,
            pos.x(),
            pos.y(),
            pos.z(),
            offset.x.toFloat(),
            offset.y.toFloat(),
            offset.z.toFloat(),
            0f,
            count
        ) {
            it.writeFloat( color.red / 255f)
            it.writeFloat( color.green / 255f)
            it.writeFloat( color.blue / 255f)
            it.writeFloat(1f)
        }
    }
}