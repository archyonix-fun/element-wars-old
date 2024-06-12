package archyonix.ewars.features.action.executors.weapon_melee

import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.item.resetCharge
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.features.pvp.WaveTask
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.network.packet.server.play.EntityAnimationPacket
import java.util.Date
import kotlin.math.roundToInt

private val cd: MutableMap<Player, Long> = HashMap()
private val charge: MutableMap<Player, Float> = HashMap()
@ClassLibrary.RegisteredClass("SWORD_MELEE_ATTACK_VERTICAL", ClassLibrary.Type.ACTION_EXECUTOR)
class SwordVerticalAttackExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {

    override fun run(player: Player, caller: Any): Boolean {
        val current = Date().time
        if(cd.containsKey(player) && current - cd[player]!! <= 300) {
            cd[player] = current
            charge[player] = (charge[player] ?: 0f) + 0.1f
            val percent = charge[player]!!

            player.showActionBar("Заряжается: ${(percent * 100).roundToInt()}%")
            if (percent < 1.0) return false
        }
        cd[player] = current
        charge.remove(player)

        if(!player.itemInMainHand.hasCharged()) return false
        player.itemInMainHand.resetCharge()

        val packet = EntityAnimationPacket(player.entityId, EntityAnimationPacket.Animation.SWING_MAIN_ARM)
        player.viewers.forEach { it.sendPacket(packet) }
        player.sendPacket(packet)

        val position = player.position.add(0.0, 1.0, 0.0)
        WaveTask(
            position,
            player.position.yaw,
            WaveTask.Type.VERTICAL,
            startRadius = 0.5,
            endRadius = 4.0,
            step = 0.5
        )
            .run { targets ->
                targets
                    .filter { it != player }
                    .forEach {
                    val tackDamageAnimation = EntityAnimationPacket(it.entityId, EntityAnimationPacket.Animation.CRITICAL_EFFECT)
                    it.viewers.forEach { viewer -> viewer.sendPacket(tackDamageAnimation) }
                    it.damage(DamageType.PLAYER_ATTACK, 1f)
                }
            }
        return true
    }
}