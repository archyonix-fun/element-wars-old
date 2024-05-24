package archyonix.ewars.features.shop

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.game
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class Dependence(
    private val type: DependenceType,
    private val targets: Set<String>
) {
    companion object {
        fun import(section: ConfigurationSection): List<Dependence> {
            return section.getStringList("dependencies")
                .map {
                    val split = it.split(":")
                    val depType = DependenceType.valueOf(split[0])
                    val targets = split.subList(1, split.size).toSet()

                    Dependence(depType, targets)
                }
        }
    }

    private var variants: List<Structure.Config.Variant>? = null

    fun generateLore(): String {
        if (variants == null) variants = findVariants()

        val names = mutableListOf<String>()
        variants!!.forEach { names.add(it.name) }
        return "${type.prefix}${names.joinToString(" или ")}"
    }

    private fun findVariants(): List<Structure.Config.Variant> {
        val variants = mutableListOf<Structure.Config.Variant>()
        for (target in targets) {
            val variant = game.structureManager.configMap().values
                .find { config -> config.variants.find { it.id == target } != null }
                ?.variants
                ?.find { it.id == target }
            if (variant != null) variants.add(variant)
        }
        return variants
    }

    fun check(team: GameTeam): Boolean {
        return when (type) {
            DependenceType.STRUCTURE -> {
                val structure = game.structureManager
                    .findStructures(team)
                    .filter { it.getProcess().type != Structure.Process.Type.BUILDING }
                    .find { targets.contains(it.getVariant()?.id) }
                structure != null
            }
        }
    }

    // Child classes
    enum class DependenceType(
        val prefix: String
    ) {
        STRUCTURE("<text>Здание - "),
    }
}