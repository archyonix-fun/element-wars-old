package archyonix.ewars.entities.structure

import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.shared.libs.*
import archyonix.ewars.structureManager
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

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
    val dependencies = config.loadDependencies()
    val costInfo = config.loadCost()

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
}