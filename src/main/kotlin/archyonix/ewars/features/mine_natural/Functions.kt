package archyonix.ewars.features.mine_natural

import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.block.Block

fun breakTree(position: Point) {
    val zero = findZeroLogBlockPosition(position)

    val type = game.instance.getBlock(zero).name()

    val min = zero.sub(3.0, .0, 3.0)
    val max = zero.add(3.0, 12.0, 3.0)

    for (x in min.blockX()..max.blockX()) {
        for (z in min.blockZ()..max.blockZ()) {
            for (y in min.blockY()..max.blockY()) {
                val absoluteBlockPosition = Pos(x.toDouble(), y.toDouble(), z.toDouble())
                val block = game.instance.getBlock(absoluteBlockPosition)

                when (type) {
                    Block.SPRUCE_LOG.name() -> {
                        if (block.name() != Block.SPRUCE_LOG.name() && block.name() != Block.SPRUCE_LEAVES.name()) continue
                        game.instance.setBlock(absoluteBlockPosition, Block.AIR)
                    }

                    Block.BIRCH_LOG.name() -> {
                        if (block.name() != Block.BIRCH_LOG.name() && block.name() != Block.BIRCH_LEAVES.name()) continue
                        game.instance.setBlock(absoluteBlockPosition, Block.AIR)
                    }
                }
            }
        }
    }
}

private fun findZeroLogBlockPosition(position: Point): Point {
    for (y in position.blockY() downTo 0) {
        val newPos = Pos(position.x(), y.toDouble(), position.z())
        val block = game.instance.getBlock(newPos)
        if (block.name() != Block.SPRUCE_LOG.name() && block.name() != Block.BIRCH_LOG.name()) return newPos.add(.0, 1.0, .0)
    }
    return position
}