package archyonix.ewars.entities.structure

import archyonix.ewars.entities.resource.Resource
import archyonix.ewars.features.cost.CostInfo
import archyonix.ewars.shared.libs.AppSchema
import archyonix.ewars.shared.libs.Icons
import archyonix.ewars.structureManager
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*

class StructureVariant(
    private val structureId: String,
    val name: String,
    private val config: ConfigurationSection
) {
    private val schemaURI = config.getString("schema") ?: throw RuntimeException("Schema is null")

    val displayName = config.getString("displayName") ?: throw RuntimeException("DisplayName is null")
    val schema = AppSchema("config/structures/$structureId/$schemaURI")
    val buildPoints = config.getInt("buildPoints")
    val data = config.getConfigurationSection("data")
    val costInfo = getCost()

    fun buildLore(): List<String> {
        val structure = structureManager.getConfig(structureId)
        val lore = mutableListOf<String>()

        lore.add("")
        lore.add("<text>[<yellow>Ресурсы<text>]:")
        lore.addAll(costInfo.buildLore())
        lore.add("")
        lore.add("<text>[<blue>Прочие затраты<text>]:")
        lore.add("§f${Icons.HAMMER.content()}<text> - $buildPoints")
        lore.add("")
        lore.add("<yellow>ЛИМИТ: <text>${structure.limit}")

        return lore
    }

    private fun getCost(): CostInfo {
        val section = config.getConfigurationSection("cost") ?: return CostInfo()
        val resources: MutableMap<Resource, Int> = EnumMap(Resource::class.java)
        for (key in section.getKeys(false)) {
            val resource = Resource.values().find { it.name == key } ?: continue
            resources[resource] = section.getInt(key)
        }
        return CostInfo(resources)
    }
}