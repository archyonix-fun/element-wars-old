package archyonix.ewars.features.action.executors.weapon_melee

import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.item.resetCharge
import archyonix.ewars.entities.player.getFocus
import archyonix.ewars.entities.player.setFocus
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.pvp.FocusController
import archyonix.ewars.features.pvp.PVPManager
import archyonix.ewars.features.pvp.ShockWave
import archyonix.ewars.features.pvp.SwordWave
import archyonix.ewars.game
import archyonix.ewars.game.functions.ActionBar
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.network.packet.server.play.EntityAnimationPacket
import net.minestom.server.timer.Task
import java.time.Duration

abstract class SwordAttackExecutor(
    private val attackDirection: ShockWave.Direction,
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        val isCharging = game.pvpManager.charging(player) {
            buildWaveTask(player, true, force = true)
        }
        if(isCharging && game.pvpManager.chargeProgress(player) >= 0.2) {
            player.sendActionBar(
                ActionBar.Progress(
                    game.pvpManager.chargeProgress(player)
                ).buildComponent()
            )
        }
        buildWaveTask(player, game.pvpManager.hasCharged(player))
        return true
    }

    private fun buildWaveTask(player: Player, hasCharged: Boolean, force: Boolean = false): Task {
        val wave = SwordWave(
            position = player.position.add(0.0, 1.0, 0.0),
            direction = player.position.yaw,
            waveType = if (hasCharged) ShockWave.Type.CHARGED else ShockWave.Type.DEFAULT,
            waveDirection = attackDirection,
            offset = 0.5,
            distance = if (hasCharged) 12.0 else 4.0
        )
        return game.schedule.buildTask {
            if(!force) {
                if(wave.waveType == ShockWave.Type.CHARGED && !game.pvpManager.hasCharged(player)) return@buildTask
                if (!player.itemInMainHand.hasCharged() && wave.waveType != ShockWave.Type.CHARGED) return@buildTask
                if (game.pvpManager.calcDelay(player) < 250) return@buildTask
            }
            game.pvpManager.resetCharge(player)
            player.itemInMainHand.resetCharge()
            player.sendActionBar(Component.empty())
            player.setFocus(FocusController.Focus.NONE)
            wave.spawn { targets ->
                targets
                    .filter { it != player }
                    .forEach {
                        val currentDamageAnimation =
                            EntityAnimationPacket(player.entityId, EntityAnimationPacket.Animation.CRITICAL_EFFECT)
                        val targetDamageAnimation =
                            EntityAnimationPacket(it.entityId, EntityAnimationPacket.Animation.CRITICAL_EFFECT)
                        when (it.getFocus()) {
                            FocusController.Focus.BLUE -> {
                                it.viewers.forEach { viewer -> viewer.sendPacket(targetDamageAnimation) }

                                if (wave.waveType == ShockWave.Type.CHARGED) {
                                    it.damage(DamageType.PLAYER_ATTACK, 4f)
                                } else it.damage(DamageType.PLAYER_ATTACK, 1f)
                            }

                            FocusController.Focus.RED -> {
                                if (wave.waveType == ShockWave.Type.CHARGED) {
                                    it.viewers.forEach { viewer -> viewer.sendPacket(currentDamageAnimation) }
                                    player.damage(DamageType.PLAYER_ATTACK, 4f)
                                } else {
                                    it.viewers.forEach { viewer -> viewer.sendPacket(targetDamageAnimation) }
                                    it.damage(DamageType.PLAYER_ATTACK, 1f)
                                }
                            }

                            else -> {
                                it.viewers.forEach { viewer -> viewer.sendPacket(targetDamageAnimation) }
                                it.damage(DamageType.PLAYER_ATTACK, 1f)
                            }
                        }
                    }
            }
            player.setFocus(FocusController.Focus.NONE)
        }.delay(Duration.ofMillis(250)).schedule()
    }
}