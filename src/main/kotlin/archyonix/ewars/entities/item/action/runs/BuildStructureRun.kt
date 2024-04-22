package archyonix.ewars.entities.item.action.runs

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.ActionRun
import net.minestom.server.entity.Player

class BuildStructureRun() : ActionRun("BUILD_STRUCTURE") {
    override fun run(player: Player?, item: GameItem?, data: Map<String, String>) {
        for(key in data.keys) {
            val value = data[key] ?: "NULL"
            player?.sendMessage("$key = $value")
        }
    }
}