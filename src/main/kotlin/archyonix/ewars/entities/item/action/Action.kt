package archyonix.ewars.entities.item.action

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.functions.executors
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class Action(
    val id: String,
    private val config: ConfigurationSection
) {
    val trigger = config.getString("trigger") ?: throw RuntimeException("Trigger is null")
    private val executor = executors[config.getString("executor")] ?: throw RuntimeException("Executor is null")

    fun run(player: Player, item: GameItem) {
        if(executor.execute(player, config)) item.executeAction(player, "ACTION_SUCCESS:$id")
        else item.executeAction(player, "ACTION_FAILED:$id")
    }
}