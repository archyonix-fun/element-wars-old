package archyonix.ewars.entities.item.action.impl

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.ActionRun
import net.minestom.server.entity.Player

class BuildStructureAction() : ActionRun("BUILD_STRUCTURE") {
    override fun run(player: Player?, item: GameItem?, data: Map<String, String>) {
        for(key in data.keys) {
            val value = data[key] ?: "NULL"
            player?.sendMessage("$key = $value")
        }
    }
}