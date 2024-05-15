package archyonix.ewars.entities.item

import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.functions.hasGameItem
import net.minestom.server.item.ItemStack
import java.util.*
import kotlin.math.min


private val weaponCharge: MutableMap<ItemStack, Long> = HashMap()
fun ItemStack.getChargeValue(): Float {
    if (!hasGameItem()) return 1f
    val gameItem = asItemVariant()!!

    val currentTime = Date().time
    if (weaponCharge.contains(this)) {
        val lastTime = weaponCharge[this]!!
        val time = currentTime - lastTime
        val ticks = time / 50f

        return min(ticks / gameItem.recharge, 1f)
    }
    return 1f
}

fun ItemStack.hasCharged(): Boolean {
    if(!weaponCharge.contains(this)) {
        resetCharge()
        return true
    }
    return !(getChargeValue() < 1)
}

fun ItemStack.resetCharge() {
    weaponCharge[this] = Date().time
}