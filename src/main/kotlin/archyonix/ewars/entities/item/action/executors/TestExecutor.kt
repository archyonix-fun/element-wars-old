package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.entities.player.sendWarning
import archyonix.ewars.entities.structure.Structure
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.time.Duration

@Executor("TEST")
class TestExecutor : IExecutor {
    override fun execute(player: Player, data: ConfigurationSection): Boolean {
        val structureId = data.getString("structureId") ?: throw RuntimeException("Structure id is null")
        try {
            val structure = Structure(structureId, player)
            recursive(structure)
        } catch (e: Exception) {
            player.sendWarning(e.message ?: "Неизвестная ошибка")
        }
        return true
    }

    private fun recursive(structure: Structure) {
        if (structure.hasBuilding()) {
            structure.buildOrRepair(1)
            MinecraftServer.getSchedulerManager()
                .buildTask { recursive(structure) }
                .delay(Duration.ofSeconds(1))
                .schedule()
        }
    }
}