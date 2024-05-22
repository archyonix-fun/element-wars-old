package archyonix.ewars.entities.region

import archyonix.ewars.entities.region.api.Region
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.block.Block
import java.util.*

class Zone(
    private val startX: Int,
    private val startY: Int,
    private val startZ: Int,
    private val endX: Int,
    private val endY: Int,
    private val endZ: Int,
) {
    constructor(startPoint: Point, endPoint: Point) : this(
        startPoint.blockX(), startPoint.blockY(), startPoint.blockZ(),
        endPoint.blockX(), endPoint.blockY(), endPoint.blockZ()
    )
    constructor(region: Region): this(region.startPoint(), region.endPoint())

    fun notSolidBlockCount(limit: Int = Int.MAX_VALUE): Int {
        var count = 0
        val instance = game.instance
        for (y in startY..endY) {
            for (x in startX..endX) {
                for (z in startZ..endZ) {
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
        for (y in startY..endY) {
            for (x in startX..endX) {
                for (z in startZ..endZ) {
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
        for (y in startY..endY) {
            for (x in startX..endX) {
                for (z in startZ..endZ) {
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
        return "Zone(minX: $startX, minY: $startY, minZ: $startZ, maxX: $endX, maxY: $endY, maxZ: $endZ)"
    }
}