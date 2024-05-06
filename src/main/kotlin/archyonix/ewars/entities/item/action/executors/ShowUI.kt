package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.game.ui.BuildMenuUI
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@Executor("SHOW_UI")
class ShowUI : IExecutor {
    override fun execute(player: Player, data: ConfigurationSection): Boolean {
        val uiId = data.getString("id") ?: throw RuntimeException("UI id is null")
        when (uiId) {
            "BUILD_MENU" -> BuildMenuUI(player).show()
            else -> return true
        }
        return true
    }
}