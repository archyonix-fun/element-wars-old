package archyonix.ewars.features.dependencies

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.game
import net.minestom.server.entity.Player
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

    fun generateLore(): String? {
        return when(type) {
            DependenceType.STRUCTURE -> {
                if (variants == null) variants = findVariants()

                val names = mutableListOf<String>()
                variants!!.forEach { names.add(it.name) }
                "${type.prefix}${names.joinToString(" или ")}"
            }

            else -> {
                null
            }
        }
    }

    private fun findVariants(): List<Structure.Config.Variant> {
        val variants = mutableListOf<Structure.Config.Variant>()
        for (target in targets) {
            val split = target.split("-")
            val structureId = split[0]
            val variantId = split[1]

            val variant = game.structureManager.configMap()[structureId]
                ?.variants?.find { it.id == variantId }
            if (variant != null) variants.add(variant)
        }
        return variants
    }

    fun check(team: GameTeam): Boolean {
        for (target in targets) {
            val isInvert = target.startsWith("!")
            val cleanTarget = if(isInvert) target.substring(1) else target
            if (!team.conditions.checkCondition(Condition(type, cleanTarget)))
                if(isInvert) continue else return false
        }
        return true
    }

    fun check(player: Player): Boolean {
        val team = player.gameTeam()
        for (target in targets) {
            val isInvert = target.startsWith("!")
            val cleanTarget = if(isInvert) target.substring(1) else target
            val result = team.conditions.checkCondition(player, Condition(type, cleanTarget))
            if (result == isInvert) return false
        }
        return true
    }

    // Child classes
    enum class DependenceType(
        val prefix: String
    ) {
        STRUCTURE("<text>Здание - "),
        UPGRADE("<text>Улучшение - "),
    }
}