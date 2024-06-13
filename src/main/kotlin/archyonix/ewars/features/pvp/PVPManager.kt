package archyonix.ewars.features.pvp

import archyonix.ewars.entities.item.getUUID
import net.minestom.server.entity.Player
import java.util.*

class PVPManager {
    private val chargeCD: MutableMap<Player, Long> = HashMap()
    private val weaponCharge: MutableMap<UUID, Float> = HashMap()
    private val playerFocus: MutableMap<Player, Focus> = HashMap()

    fun charging(player: Player): Boolean {
        val itemId = player.itemInMainHand.getUUID() ?: return false
        val cd = chargeCD[player] ?: 0
        val current = Date().time

        chargeCD[player] = current
        if (current - cd > 200 && cd != 0L) {
            weaponCharge.remove(itemId)
            chargeCD.remove(player)
            return false
        }
        val charge = weaponCharge[itemId] ?: 0f
        if (charge > 2f) return false
        weaponCharge[itemId] = charge + 0.1f
        return true
    }

    fun chargeProgress(player: Player): Float {
        val itemId = player.itemInMainHand.getUUID() ?: return 0f
        return weaponCharge[itemId] ?: 0f
    }

    fun hasCharged(player: Player): Boolean {
        val itemId = player.itemInMainHand.getUUID() ?: return false
        val charge = weaponCharge[itemId] ?: 0f
        return charge >= 10f
    }

    fun resetCharge(player: Player) {
        val itemId = player.itemInMainHand.getUUID() ?: return
        weaponCharge.remove(itemId)
        chargeCD.remove(player)
    }

    fun getFocus(player: Player): Focus =
        playerFocus[player] ?: Focus.NONE

    fun setFocus(player: Player, focus: Focus) {
        playerFocus[player] = focus
    }

    enum class Focus { NONE, BLUE, RED }
}