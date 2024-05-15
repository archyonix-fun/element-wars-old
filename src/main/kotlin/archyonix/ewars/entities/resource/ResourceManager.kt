package archyonix.ewars.entities.resource

import archyonix.ewars.entities.resource.api.Extractor
import archyonix.ewars.entities.resource.extractors.DefaultExtractor
import archyonix.ewars.entities.resource.extractors.TreeExtractor
import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class ResourceManager {
    private val configPath = "config/extraction.yaml"
    private val extractors = mutableMapOf<String, Extractor>()
    private val extractorClasses = mutableMapOf(
        Pair("DEFAULT", DefaultExtractor::class.java),
        Pair("TREE", TreeExtractor::class.java),
    )

    init {
        val config = YamlConfiguration.loadConfiguration(File(configPath))
        for (id in config.getKeys(false)) {
            val section = config.getConfigurationSection(id)!!
            val extractorName = section.getString("extractor") ?: continue
            val extractorClass = extractorClasses[extractorName] ?: continue

            extractors[id] = extractorClass
                .getDeclaredConstructor(ConfigurationSection::class.java)
                .newInstance(section)
        }
    }

    fun extract(point: Point, union: String): Resource? {
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