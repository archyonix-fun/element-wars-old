package archyonix.ewars.entities.structure

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class StructureData(
    val id: String
) {
    private val config = YamlConfiguration.loadConfiguration(File("config/structures/$id/structure.yaml"))
    val area: Int = config.getInt("area")
    val variants = readVariants()

    private fun readVariants(): Map<String, StructureVariant> {
        val variants: MutableMap<String, StructureVariant> = HashMap()
        val variantListSection = config.getConfigurationSection("variants") ?: return variants
        for(key in variantListSection.getKeys(false)) {
            val variantSection = variantListSection.getConfigurationSection(key) ?: continue
            variants[key] = StructureVariant(id, variantSection)
        }
        return variants
    }
}