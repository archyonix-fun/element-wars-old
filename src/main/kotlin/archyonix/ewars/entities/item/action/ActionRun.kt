package archyonix.ewars.entities.item.action

import archyonix.ewars.entities.item.GameItem
import net.minestom.server.entity.Player

abstract class ActionRun(val id: String) {
    abstract fun run(player: Player? = null, item: GameItem? = null, data: Map<String, String>)
}