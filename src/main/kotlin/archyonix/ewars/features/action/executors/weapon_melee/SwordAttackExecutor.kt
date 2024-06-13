package archyonix.ewars.features.action.executors.weapon_melee

import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.item.resetCharge
import archyonix.ewars.entities.player.getFocus
import archyonix.ewars.entities.player.setFocus
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.pvp.PVPManager
import archyonix.ewars.features.pvp.ShockWave
import archyonix.ewars.features.pvp.SwordWave
import archyonix.ewars.game
import archyonix.ewars.game.functions.ActionBar
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.network.packet.server.play.EntityAnimationPacket
import net.minestom.server.timer.Task
import java.time.Duration

abstract class SwordAttackExecutor(
    private val attackDirection: ShockWave.Direction,
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val waveReg: MutableMap<Player, Task> = HashMap()
    override fun run(player: Player, caller: Any): Boolean {
        if (player.isSneaking) {
            player.setFocus(PVPManager.Focus.RED)
            game.schedule.buildTask {
                player.setFocus(PVPManager.Focus.NONE)
            }.delay(Duration.ofMillis(300)).schedule()
            return false
        }

        if (game.pvpManager.charging(player)) {
            player.sendActionBar(
                ActionBar.Progress(
                    game.pvpManager.chargeProgress(player)
                ).buildComponent()
            )
            if (waveReg.containsKey(player)) {
                player.setFocus(PVPManager.Focus.BLUE)
                waveReg[player]?.cancel()
            }
        }
        if (waveReg.containsKey(player)) return false
        waveReg[player] = buildWaveTask(player)
        return true
    }

    private fun buildWaveTask(player: Player): Task {
        val hasCharged = game.pvpManager.hasCharged(player)
        val wave = SwordWave(
            position = player.position.add(0.0, 1.0, 0.0),
            direction = player.position.yaw,
            waveType = if (hasCharged) ShockWave.Type.CHARGED else ShockWave.Type.DEFAULT,
            waveDirection = attackDirection,
            offset = 0.5,
            distance = if (hasCharged) 8.0 else 4.0
        )
        return game.schedule.buildTask {
            if (!player.itemInMainHand.hasCharged() && game.pvpManager.chargeProgress(player) == 0f) return@buildTask
            player.itemInMainHand.resetCharge()
            player.setFocus(PVPManager.Focus.NONE)

            val packet = EntityAnimationPacket(player.entityId, EntityAnimationPacket.Animation.SWING_MAIN_ARM)
            player.viewers.forEach { it.sendPacket(packet) }
            player.sendPacket(packet)

            wave.spawn { targets ->
                targets
                    .filter { it != player }
                    .forEach {
                        val currentDamageAnimation =
                            EntityAnimationPacket(player.entityId, EntityAnimationPacket.Animation.CRITICAL_EFFECT)
                        val targetDamageAnimation =
                            EntityAnimationPacket(it.entityId, EntityAnimationPacket.Animation.CRITICAL_EFFECT)
                        when (it.getFocus()) {
                            PVPManager.Focus.BLUE -> {
                                it.viewers.forEach { viewer -> viewer.sendPacket(targetDamageAnimation) }

                                if (wave.waveType == ShockWave.Type.CHARGED) {
                                    it.damage(DamageType.PLAYER_ATTACK, 4f)
                                } else it.damage(DamageType.PLAYER_ATTACK, 1f)
                            }

                            PVPManager.Focus.RED -> {
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
        }.delay(Duration.ofMillis(250)).schedule()
    }
}