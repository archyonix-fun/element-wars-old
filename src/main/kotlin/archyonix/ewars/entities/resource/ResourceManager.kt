package archyonix.ewars.entities.resource

import archyonix.ewars.entities.resource.api.Extractor
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class ResourceManager {
    private val configPath = "config/extraction.yaml"
    private val extractors = mutableMapOf<String, Extractor>()

    init {
        val config = YamlConfiguration.loadConfiguration(File(configPath))
        for (id in config.getKeys(false)) {
            val section = config.getConfigurationSection(id)!!
            val extractorName = section.getString("extractor") ?: continue
            val extractorConstructor = game.classLibrary
                .findConstructor<Extractor>(extractorName, ClassLibrary.Type.RESOURCE_EXTRACTOR) ?: continue

            extractors[id] = extractorConstructor.newInstance(section)
        }
    }

    fun extract(point: Point, union: String): MutableSet<Extractor.ResourceVolume>? {
        game.instance.loadChunk(point).get().run {
            val block = getBlock(point)
            val extractor = extractors.values
                .filter { it.containsToolUnion(union) }
                .find { it.containsBlock(block.name()) } ?: return null
            extractor.run(point, union)
            return extractor.resource
        }
    }
}