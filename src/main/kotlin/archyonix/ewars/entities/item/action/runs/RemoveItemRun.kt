package archyonix.ewars.entities.item.action.runs

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.ActionRun
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack

class RemoveItemRun : ActionRun("REMOVE_ITEM") {
    override fun run(player: Player?, item: GameItem?, data: Map<String, String>) {
        when (Target.valueOf(data["target"]!!) ?: Target.THIS) {
            Target.THIS -> player!!.itemInMainHand = ItemStack.AIR
        }
    }

    enum class Target {
        THIS
    }
}