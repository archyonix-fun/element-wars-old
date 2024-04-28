package archyonix.ewars.entities.item.action

import archyonix.ewars.entities.item.action.runs.CreateStructureRun
import archyonix.ewars.entities.item.action.runs.RemoveItemRun
import net.minestom.server.entity.Player

class ActionManager {

    private val actionRuns: MutableMap<String, ActionRun> = HashMap()
    private val actions: MutableSet<Action> = HashSet()

    init {
        addRun(CreateStructureRun())
        addRun(RemoveItemRun())
    }

    fun addAction(action: Action) = actions.add(action)
    private fun addRun(actionRun: ActionRun) {
        actionRuns[actionRun.id] = actionRun
    }

    fun call(trigger: GameTrigger, player: Player?) {
        actions.filter { it.trigger == trigger }.forEach { it.run(player) }
    }

    fun findActionRunById(actionId: String): ActionRun {
        return actionRuns[actionId]!!
    }
}