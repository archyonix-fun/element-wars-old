package archyonix.ewars.features.battle.handler

import archyonix.ewars.entities.item.enums.ItemFlag
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.features.battle.enums.Focus
import archyonix.ewars.features.battle.events.WeaponChargedAttackEvent
import archyonix.ewars.features.battle.events.WeaponChargingEvent
import archyonix.ewars.features.battle.events.WeaponResetChargeEvent
import archyonix.ewars.game
import archyonix.ewars.game.events.GameTickEvent
import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import java.util.*

class ChargeHandler : PublicEventNode("ChargeHandler") {
    private val playerMap: MutableMap<Player, Long> = HashMap()
    private val focusMap: MutableMap<Player, Pair<Focus, Int>> = HashMap()
    private val chargeMap: MutableMap<Player, Int> = HashMap()

    init {
        addListener(PlayerMoveEvent::class.java) {
            val player = it.player
            if (player.position.asVec() == it.newPosition.asVec()) return@addListener
            resetCharge(player, true)
        }
        addListener(PlayerChangeHeldSlotEvent::class.java) {
            resetCharge(it.player, true)
        }
        addListener(ItemDropEvent::class.java) {
            val player = it.player
            val item = it.itemStack.asGameItem() ?: return@addListener

            if (!item.flags.contains(ItemFlag.WEAPON_MELEE)) return@addListener
            resetCharge(it.player, true)
            useFocus(player, Focus.RED)
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
                else if (time - lastUseTime > 1000) resetCharge(player)

                displayFocus(player)
            }
        }
    }

    private fun charge(player: Player) {
        val level = chargeMap.getOrDefault(player, 0) + 1
        game.handler.call(WeaponChargingEvent(player, level))
        chargeMap[player] = level

        if (level > 1) useFocus(player, Focus.BLUE)
    }

    private fun resetCharge(player: Player, isCanceled: Boolean = false) {
        game.handler.call(WeaponResetChargeEvent(player, chargeMap[player] ?: 0))
        if (!isCanceled && chargeMap.containsKey(player)) {
            val level = chargeMap[player] ?: 0
            game.handler.call(WeaponChargedAttackEvent(player, level))
        }
        playerMap.remove(player)
        chargeMap.remove(player)
    }

    private fun useFocus(player: Player, focus: Focus): Boolean {
        val current = focusMap[player]
        if (current != null) return false

        focusMap[player] = Pair(focus, 2)
        return true
    }

    private fun displayFocus(player: Player) {
        val current = focusMap[player]
        if (current == null) {
            player.isGlowing = false
            return
        }
        player.team?.updateTeamColor(current.first.color)
        player.isGlowing = true

        val time = current.second - 1
        if (time >= 0) focusMap[player] = Pair(current.first, time)
        else focusMap.remove(player)
    }
}