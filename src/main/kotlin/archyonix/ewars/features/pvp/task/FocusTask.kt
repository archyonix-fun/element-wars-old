package archyonix.ewars.features.pvp.task

import archyonix.ewars.entities.player.getFocus
import archyonix.ewars.features.pvp.PVPManager
import archyonix.ewars.game
import net.minestom.server.particle.Particle
import net.minestom.server.particle.ParticleCreator
import java.time.Duration

class FocusTask : Runnable {
    override fun run() {
        for (player in game.getPlayers()) {
            val focus = player.getFocus()
            if (focus == PVPManager.Focus.NONE) continue
            val center = player.position.add(0.0, 1.0, 0.0)
            val packet = ParticleCreator.createParticlePacket(
                Particle.DUST,
                true,
                center.x,
                center.y,
                center.z,
                0.5f,
                1f,
                0.5f,
                0f,
                8
            ) {
                it.writeFloat(if (focus == PVPManager.Focus.RED) 1f else 0f)
                it.writeFloat(0f)
                it.writeFloat(if (focus == PVPManager.Focus.BLUE) 1f else 0f)
                it.writeFloat(1f)
            }
            game.getPlayers().forEach { it.sendPacket(packet) }
        }
        game.schedule.buildTask(this::run).delay(Duration.ofMillis(100)).schedule()
    }
}