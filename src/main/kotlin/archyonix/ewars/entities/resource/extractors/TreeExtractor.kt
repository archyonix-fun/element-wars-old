package archyonix.ewars.entities.resource.extractors

import archyonix.ewars.entities.resource.api.Extractor
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.block.Block
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@ClassLibrary.RegisteredClass("TREE", ClassLibrary.Type.RESOURCE_EXTRACTOR)
class TreeExtractor(
    config: ConfigurationSection
) : Extractor(config) {
    override fun run(point: Point, toolGroup: String): Boolean {
        instance().loadChunk(point).get().run {
            val block = getBlock(point)
            if (!containsBlock(block.name())) return false
            if (!containsToolUnion(toolGroup)) return false

            val zeroPoint = findZeroBlockPoint(point)
            breakTree(zeroPoint)
            return true
        }
    }

    private fun findZeroBlockPoint(point: Point): Point {
        for (y in point.blockY() downTo 0) {
            val newPos = Pos(point.x(), y.toDouble(), point.z())
            val block = instance().getBlock(newPos)
            if (!containsBlock(block.name())) return newPos.add(.0, 1.0, .0)
        }
        return point
    }

    private fun breakTree(zero: Point) {
        val min = zero.sub(3.0, .0, 3.0)
        val max = zero.add(3.0, 12.0, 3.0)
        val log = instance().getBlock(zero).namespace().path().split("_")[0]
        for (x in min.blockX()..max.blockX()) {
            for (z in min.blockZ()..max.blockZ()) {
                for (y in min.blockY()..max.blockY()) {
                    val absolutePoint = Pos(x.toDouble(), y.toDouble(), z.toDouble())
                    val block = instance().getBlock(absolutePoint)

                    if (
                        block.name().contains("${log}_leaves") ||
                        (
                                absolutePoint.blockX() == zero.blockX()
                                        &&
                                        absolutePoint.blockZ() == zero.blockZ()
                                )
                    ) game.instance.setBlock(absolutePoint, Block.fromNamespaceId(swap)!!)
                }
            }
        }
    }
}