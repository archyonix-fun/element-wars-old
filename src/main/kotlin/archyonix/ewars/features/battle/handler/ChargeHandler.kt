package archyonix.ewars.features.battle.handler

import archyonix.ewars.entities.item.enums.ItemFlag
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.entities.player.setFocus
import archyonix.ewars.features.battle.events.WeaponChargingEvent
import archyonix.ewars.features.battle.events.WeaponResetChargeEvent
import archyonix.ewars.features.pvp.FocusController
import archyonix.ewars.game
import archyonix.ewars.game.events.GameTickEvent
import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import java.util.*

class ChargeHandler : PublicEventNode("ChargeHandler") {
    private val playerMap: MutableMap<Player, Long> = HashMap()
    private val chargeMap: MutableMap<Player, Int> = HashMap()

    init {
        addListener(PlayerMoveEvent::class.java) {
            val player = it.player
            if (player.position.asVec() == it.newPosition.asVec()) return@addListener
            resetCharge(player)
        }
        addListener(PlayerChangeHeldSlotEvent::class.java) {
            resetCharge(it.player)
        }
        addListener(PlayerUseItemEvent::class.java) {
            val player = it.player
            val item = it.itemStack.asGameItem() ?: return@addListener

            if (!item.flags.contains(ItemFlag.WEAPON_MELEE)) return@addListener
            playerMap[player] = Date().time
        }
        addListener(GameTickEvent::class.java) {
            val time = Date().time
            playerMap.forEach { (player, lastUseTime) ->
                if (time - lastUseTime <= 250) charge(player)
                else resetCharge(player)
            }
        }
    }

    private fun charge(player: Player) {
        val level = chargeMap.getOrDefault(player, 0) + 1
        game.handler.call(WeaponChargingEvent(player, level))
        chargeMap[player] = level
    }

    private fun resetCharge(player: Player) {
        game.handler.call(WeaponResetChargeEvent(player, chargeMap[player] ?: 0))
        playerMap.remove(player)
        chargeMap.remove(player)
    }
}