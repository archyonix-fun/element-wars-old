package archyonix.ewars.features.action.executors.team

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.features.dependencies.Condition
import archyonix.ewars.features.dependencies.Dependence
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("TEAM_CONDITION_REMOVE", ClassLibrary.Type.ACTION_EXECUTOR)
class RemoveCondition(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val isTeam: Boolean = (properties["isTeam"] ?: false) as Boolean
    private val condition: String = (properties["condition"]
        ?: throw RuntimeException("Condition is null")) as String

    override fun run(player: Player, caller: Any): Boolean {
        val split = condition.split(":")
        if(split.size != 2) return false

        val conditions = player.gameTeam().conditions
        val condition = Condition(
            Dependence.DependenceType.valueOf(split[0].uppercase()),
            split[1]
        )
        if(isTeam) conditions.removeCondition(condition)
        else conditions.removeCondition(player, condition)
        return true
    }
}