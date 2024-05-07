package archyonix.ewars.game.task

import archyonix.ewars.game
import archyonix.ewars.structureManager
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.tag.Tag
import java.time.Duration

class StructureAreaTask : Runnable {
    private val validBlocks = setOf(
        Block.BROWN_MUSHROOM.name(),
        Block.SUGAR_CANE.name(),
        Block.SHORT_GRASS.name()
    )

    override fun run() {
        game.getPlayers()
            .filter { it.isOnGround }
            .filter { it.itemInMainHand.hasTag(Tag.String("structureId")) }
            .forEach {
                val itemStack = it.itemInMainHand
                val structureId = itemStack.getTag(Tag.String("structureId"))
                val structureConfig = structureManager.getConfig(structureId)
                val zeroPosition = it.position.asVec()

                val minX = zeroPosition.blockX() - structureConfig.radius
                val maxX = zeroPosition.blockX() + structureConfig.radius

                val minZ = zeroPosition.blockZ() - structureConfig.radius
                val maxZ = zeroPosition.blockZ() + structureConfig.radius

                for (x in minX..maxX) {
                    for (z in minZ..maxZ) {
                        val pos = Pos(x + .5, zeroPosition.y, z + .5)
                        when {
                            x == minX -> showEffect(it, pos)
                            x == maxX -> showEffect(it, pos)
                            z == minZ -> showEffect(it, pos)
                            z == maxZ -> showEffect(it, pos)
                        }
                    }
                }
            }

        game.schedule.buildTask(this).delay(Duration.ofMillis(200)).schedule()
    }

    private fun showEffect(player: Player, pos: Pos) {
        player.sendPacket(
            ParticlePacket(
                Particle.FLAME.id(),
                false,
                pos.x,
                pos.y,
                pos.z,
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