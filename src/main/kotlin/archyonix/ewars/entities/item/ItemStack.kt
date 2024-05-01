package archyonix.ewars.entities.item

import archyonix.ewars.entities.item.functions.findItemById
import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.functions.getGameItemId
import archyonix.ewars.entities.item.functions.hasGameItem
import net.minestom.server.item.ItemStack
import java.util.*
import kotlin.math.min


private val weaponCharge: MutableMap<ItemStack, Long> = HashMap()
fun ItemStack.getChargeValue(): Float {
    if (!hasGameItem()) return 1f
    val gameItem = getGameItem()!!

    val currentTime = Date().time
    if (weaponCharge.contains(this)) {
        val lastTime = weaponCharge[this]!!
        val time = currentTime - lastTime
        val ticks = time / 50

        return min(ticks / gameItem.attackSpeed, 1f)
    }
    return 1f
}

fun ItemStack.resetCharge() {
    weaponCharge[this] = Date().time
}