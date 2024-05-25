package archyonix.ewars.features.action.executors.item

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack

@ClassLibrary.RegisteredClass("ITEM_REMOVE", ClassLibrary.Type.ACTION_EXECUTOR)
class RemoveExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val amount = properties.getOrDefault("amount", 1) as Int
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem.Variant) throw RuntimeException("Executor can only be called by a game item")
        val item = player.itemInMainHand

        if (item.amount() < amount) return false
        player.itemInMainHand =
            if (item.amount() > amount) item.withAmount(item.amount() - amount)
            else ItemStack.AIR
        return true
    }
}