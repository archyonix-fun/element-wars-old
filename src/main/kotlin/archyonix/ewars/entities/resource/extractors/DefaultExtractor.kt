package archyonix.ewars.entities.resource.extractors

import archyonix.ewars.entities.resource.api.Extractor
import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import net.minestom.server.instance.block.Block
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.time.Duration

class DefaultExtractor(
    config: ConfigurationSection
) : Extractor(config) {
    override fun run(point: Point, toolGroup: String): Boolean {
        val block = instance().getBlock(point).name()
        if (!containsBlock(block)) return false
        if (!containsToolUnion(toolGroup)) return false

        instance().setBlock(point, Block.fromNamespaceId(swap)!!)
        if (repair) game.schedule.buildTask {
            instance().setBlock(point, Block.fromNamespaceId(block)!!)
        }.delay(
            Duration.ofSeconds(seconds.toLong())
        ).schedule()
        return true
    }
}