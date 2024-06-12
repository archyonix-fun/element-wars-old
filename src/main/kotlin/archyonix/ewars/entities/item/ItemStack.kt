package archyonix.ewars.entities.item

import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.functions.hasGameItem
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import org.jetbrains.annotations.UnknownNullability
import java.util.*
import kotlin.math.min


private val weaponCharge: MutableMap<UUID, Long> = HashMap()
fun ItemStack.getChargeValue(): Float {
    if (!hasGameItem()) return 1f
    val gameItem = asItemVariant()!!

    val currentTime = Date().time
    if (weaponCharge.contains(getUUID())) {
        val lastTime = weaponCharge[getUUID()]!!
        val time = currentTime - lastTime
        val ticks = time / 50f

        return min(ticks / gameItem.recharge, 1f)
    }
    return 1f
}

fun ItemStack.hasCharged(): Boolean {
    if (!weaponCharge.contains(getUUID())) {
        resetCharge()
        return true
    }
    return !(getChargeValue() < 1.0f)
}

fun ItemStack.resetCharge() {
    weaponCharge[getUUID()!!] = Date().time
}

fun ItemStack.getUUID(): @UnknownNullability UUID? {
    return getTag(Tag.UUID("uuid"))
}