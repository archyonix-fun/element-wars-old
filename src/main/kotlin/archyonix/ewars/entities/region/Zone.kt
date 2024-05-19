package archyonix.ewars.entities.region

import archyonix.ewars.game
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.block.Block
import java.util.*

class Zone(
    private val minX: Int,
    private val minY: Int,
    private val minZ: Int,
    private val maxX: Int,
    private val maxY: Int,
    private val maxZ: Int,
) {
    companion object {
        fun fromRegion3D(region3D: Region3D): Zone =
            Zone(
                region3D.startX, region3D.startY, region3D.startZ,
                region3D.endX, region3D.endY, region3D.endZ
            )
    }

    fun notSolidBlockCount(limit: Int = Int.MAX_VALUE): Int {
        var count = 0
        val instance = game.instance
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    if (count >= limit) return count
                    val absoluteBlockPosition = Vec(x.toDouble(), y.toDouble(), z.toDouble())
                    instance.loadChunk(absoluteBlockPosition).thenRun {
                        val block = instance.getBlock(absoluteBlockPosition)

                        if (!block.isSolid) count++
                    }
                }
            }
        }
        return count
    }

    fun solidBlockCount(limit: Int = Int.MAX_VALUE): Int {
        var count = 0
        val instance = game.instance
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    if (count >= limit) return count
                    val absoluteBlockPosition = Vec(x.toDouble(), y.toDouble(), z.toDouble())
                    instance.loadChunk(absoluteBlockPosition).thenRun {
                        val block = instance.getBlock(absoluteBlockPosition)

                        if (block.isSolid || block.isLiquid) count++
                    }
                }
            }
        }
        return count
    }

    fun fill(vararg blocks: Block) {
        val random = Random()
        val instance = game.instance
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    val block = blocks[random.nextInt(blocks.size)]
                    val absoluteBlockPosition = Vec(x.toDouble(), y.toDouble(), z.toDouble())
                    instance.loadChunk(absoluteBlockPosition).thenRun {
                        instance.setBlock(absoluteBlockPosition, block)
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return "Zone(minX: $minX, minY: $minY, minZ: $minZ, maxX: $maxX, maxY: $maxY, maxZ: $maxZ)"
    }
}