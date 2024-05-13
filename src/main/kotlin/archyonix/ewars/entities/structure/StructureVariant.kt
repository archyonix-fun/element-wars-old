package archyonix.ewars.entities.structure

import archyonix.ewars.entities.resource.Resource
import archyonix.ewars.entities.structure.enums.DependenceType
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.features.cost.CostInfo
import archyonix.ewars.shared.libs.AppSchema
import archyonix.ewars.shared.libs.Icons
import archyonix.ewars.shared.libs.LoreUtils
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
    val dependencies = loadDependencies()
    val costInfo = getCost()

    fun buildLore(team: GameTeam): List<String> {
        val structure = structureManager.getConfig(structureId)
        val lore = mutableListOf<String>()

        lore.addAll(LoreUtils.buildCostLore(costInfo))
        lore.addAll(LoreUtils.buildDependenciesLore(dependencies, team))
        lore.add("")
        lore.add("<text>[<blue>Прочие затраты<text>]:")
        lore.add("§f${Icons.HAMMER.content()}<text> - $buildPoints")
        if (structure.limit > 0) {
            lore.add("")
            lore.add("<yellow>ЛИМИТ: <text>${structure.limit}")
        }

        return lore
    }

    fun checkDependencies(team: GameTeam): Boolean {
        for (dep in dependencies) {
            if (!dep.check(team)) return false
        }
        return true
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


    private fun loadDependencies(): List<Dependence> {
        return config.getStringList("dependencies")
            .map {
                val split = it.split(":")
                val depType = DependenceType.valueOf(split[0])
                val targets = split.subList(1, split.size).toSet()

                Dependence(depType, targets)
            }
    }
}