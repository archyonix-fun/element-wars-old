package archyonix.ewars.features.pvp

import archyonix.ewars.entities.item.getChargeValue
import archyonix.ewars.entities.item.getUUID
import archyonix.ewars.entities.item.hasCharged
import net.minestom.server.entity.Player
import java.util.*

class PVPManager {
    private val chargeDelay: MutableMap<Player, Long> = HashMap()
    private val weaponCharge: MutableMap<UUID, Float> = HashMap()
    val focus: FocusController = FocusController()

    fun charging(player: Player, overload: () -> Unit = {}): Boolean {
        val current = Date().time
        val delay = chargeDelay[player] ?: 0
        val itemId = player.itemInMainHand.getUUID() ?: return false
        chargeDelay[player] = current

        if (current - delay < 150 || player.itemInMainHand.getChargeValue() < 1f) return false
        val charge = weaponCharge[itemId] ?: 0f
        if (charge > 2f) {
            overload()
            weaponCharge.remove(itemId)
            return false
        }
        weaponCharge[itemId] = charge + 0.1f
        return true
    }

    fun calcDelay(player: Player): Long {
        val delay = chargeDelay[player] ?: 0
        return Date().time - delay
    }

    fun chargeProgress(player: Player): Float {
        val itemId = player.itemInMainHand.getUUID() ?: return 0f
        return weaponCharge[itemId] ?: 0f
    }

    fun hasCharged(player: Player): Boolean {
        val itemId = player.itemInMainHand.getUUID() ?: return false
        val charge = weaponCharge[itemId] ?: 0f
        return charge >= 1f
    }

    fun resetCharge(player: Player) {
        val itemId = player.itemInMainHand.getUUID() ?: return
        weaponCharge.remove(itemId)
        chargeDelay.remove(player)
    }
}