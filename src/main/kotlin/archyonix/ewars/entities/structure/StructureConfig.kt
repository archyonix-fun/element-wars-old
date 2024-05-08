package archyonix.ewars.entities.structure

import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.structureManager
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.floor

class StructureConfig(
    config: ConfigurationSection
) {
    val name = config.getString("name") ?: throw RuntimeException("Name is null")
    val limit = config.getInt("limit")
    val height = config.getInt("height")
    val area = config.getInt("area")
    val radius = floor(area / 2.0).toInt()
    val tagList = config.getStringList("tags")
    private val variantSection = config.getConfigurationSection("variants")
        ?: throw RuntimeException("Variants is null: $name")
    val variants = loadVariants()


    fun checkLimit(team: GameTeam): Boolean {
        if(limit == 0) return true
        return structureManager.structureSet
            .filter { it.team == team }
            .filter { it.name == name }
            .size < limit
    }

    private fun loadVariants(): MutableList<StructureVariant> {
        val variants: MutableList<StructureVariant> = ArrayList()
        for (variantName in variantSection.getKeys(false)) {
            val variantConfig = variantSection.getConfigurationSection(variantName)!!
            variants.add(StructureVariant(name, variantName, variantConfig))
        }
        if (variants.isEmpty()) throw RuntimeException("Variants is empty: $name")
        return variants
    }
}