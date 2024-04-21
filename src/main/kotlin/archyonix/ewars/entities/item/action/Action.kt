package archyonix.ewars.entities.item.action

import archyonix.ewars.entities.item.GameItem
import net.minestom.server.entity.Player

class Action(
    val run: ActionRun,
    val data: Map<String, String>,
    val item: GameItem,
    val trigger: GameTrigger
) {
    fun run(player: Player?) {
        run.run(player, item, data)
    }
}