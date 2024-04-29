package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.entities.player.sendInfo
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@Executor("TEST")
class TestExecutor: IExecutor {
    override fun execute(player: Player, data: ConfigurationSection): Boolean {
        player.sendInfo("Execute")
        return true
    }
}