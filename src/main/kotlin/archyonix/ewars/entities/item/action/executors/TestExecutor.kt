package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.showMessage
import archyonix.ewars.entities.structure.Structure
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@Executor("TEST")
class TestExecutor : IExecutor {
    override fun execute(player: Player, data: ConfigurationSection): Boolean {
        val structureId = data.getString("structureId") ?: throw RuntimeException("Structure id is null")
        try {
            Structure(structureId, player)
        } catch (e: Exception) {
            player.showMessage(e.message ?: "Неизвестная ошибка", MessageType.WARNING)
        }
        return true
    }
}