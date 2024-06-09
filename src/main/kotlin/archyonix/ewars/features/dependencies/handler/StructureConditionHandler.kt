package archyonix.ewars.features.dependencies.handler

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.events.StructureDeconstructEvent
import archyonix.ewars.entities.structure.events.StructureProcessFinishEvent
import archyonix.ewars.features.dependencies.Condition
import archyonix.ewars.features.dependencies.Dependence
import archyonix.ewars.game
import net.minestom.server.event.PublicEventNode

class StructureConditionHandler : PublicEventNode("StructureConditionHandler") {
    init {
        addListener(StructureProcessFinishEvent::class.java) {
            when (it.processType) {
                Structure.Process.Type.BUILDING,
                Structure.Process.Type.UPGRADE -> addConditions(it.structure)

                else -> return@addListener
            }
        }

        addListener(StructureDeconstructEvent::class.java) {
            val structures = HashSet(game.structureManager.findStructures(it.structure.team, it.structure.id))
            structures.remove(it.structure)
            
            removeConditions(it.structure)
            for (structure in structures)
                addConditions(structure)
        }
    }

    private fun addConditions(structure: Structure) {
        val index = structure.config.variants.indexOf(structure.getVariant())
        if (index == -1) return
        val variants = structure.config.variants.subList(0, index + 1)
        val conditions: MutableSet<Condition> = HashSet()
        for (variant in variants) {
            conditions.add(
                Condition(
                    Dependence.DependenceType.STRUCTURE,
                    "${structure.id}-${variant.id}"
                )
            )
        }
        for (condition in conditions)
            structure.team.conditions.addCondition(condition)
    }

    private fun removeConditions(structure: Structure) {
        val index = structure.config.variants.indexOf(structure.getVariant())
        if (index == -1) return
        val variants = structure.config.variants.subList(0, index + 1)
        val conditions: MutableSet<Condition> = HashSet()
        for (variant in variants) {
            conditions.add(
                Condition(
                    Dependence.DependenceType.STRUCTURE,
                    "${structure.id}-${variant.id}"
                )
            )
        }
        for (condition in conditions)
            structure.team.conditions.removeCondition(condition)
    }
}