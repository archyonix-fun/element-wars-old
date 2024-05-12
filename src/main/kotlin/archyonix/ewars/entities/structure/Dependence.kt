package archyonix.ewars.entities.structure

import archyonix.ewars.entities.structure.enums.DependenceType
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.structureManager

class Dependence(
    private val type: DependenceType,
    private val targets: Set<String>
) {
    private var variants: List<StructureVariant>? = null

    fun generateLore(team: GameTeam): String? {
        if(variants == null) variants = findVariants()

        if(check(team)) return null

        val names = mutableListOf<String>()
        variants!!.forEach { names.add(it.displayName) }
        return "${type.prefix}${names.joinToString(" или ")}"
    }

    private fun findVariants(): List<StructureVariant> {
        val variants = mutableListOf<StructureVariant>()
        for (target in targets) {
            val variant = structureManager.configMap.values
                .find { config -> config.variants.find { it.name == target } != null }
                ?.variants
                ?.find { it.name == target }
            if (variant != null) variants.add(variant)
        }
        return variants
    }

    fun check(team: GameTeam): Boolean {
        return when (type) {
            DependenceType.STRUCTURE -> {
                val structure = structureManager
                    .findStructures(team)
                    .find { targets.contains(it.variant.name) }
                structure != null
            }
        }
    }
}