package archyonix.ewars.entities.item.action

import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

interface IExecutor {
    fun execute(player: Player, data: ConfigurationSection): Boolean
}