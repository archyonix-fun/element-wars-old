package archyonix.ewars.entities.structure.task

import archyonix.ewars.entities.item.asStructureBuildItem
import archyonix.ewars.entities.item.hasStructureBuildItem
import archyonix.ewars.entities.structure.functions.withWallGrid
import archyonix.ewars.game
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import java.time.Duration

class StructureAreaTask : Runnable {
    override fun run() {
        game.getPlayers()
            .filter { it.isOnGround }
            .filter { it.itemInMainHand.hasStructureBuildItem() }
            .forEach {
                val buildItem = it.itemInMainHand.asStructureBuildItem()!!
                val structureConfig = buildItem.config
                var zeroPosition = it.position.asVec()
                if(structureConfig.name == "wall") zeroPosition = zeroPosition.withWallGrid() as Vec
                val isEven = structureConfig.area % 2 == 0

                val minX = zeroPosition.blockX() - structureConfig.radius
                val maxX = zeroPosition.blockX() + structureConfig.radius - if(isEven) 1 else 0

                val minZ = zeroPosition.blockZ() - structureConfig.radius
                val maxZ = zeroPosition.blockZ() + structureConfig.radius - if(isEven) 1 else 0

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