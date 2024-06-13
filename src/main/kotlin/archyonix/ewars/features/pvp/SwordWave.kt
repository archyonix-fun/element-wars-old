package archyonix.ewars.features.pvp

import archyonix.ewars.game
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent
import net.minestom.server.timer.TaskSchedule
import java.util.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class SwordWave(
    position: Point,
    direction: Float,
    waveType: Type,
    waveDirection: Direction,
    val offset: Double,
    distance: Double
) : ShockWave(position, direction, waveType, waveDirection, offset + distance) {
    private val multipliers = arrayOf(-2.0, -1.5, 1.5, 2.0)
    private val random: Random = Random()
    override fun spawn(callback: (targets: Set<Player>) -> Unit) {
        var r = offset
        val multiplier = multipliers[random.nextInt(multipliers.size)]
        game.schedule.submitTask {
            if (r <= this.radius && !isCancel) {
                drawArc(r, multiplier)
                r += 0.5
                return@submitTask TaskSchedule.millis(25)
            } else callback(targets)
            cancel()
            return@submitTask null
        }
    }

    private fun drawArc(radius: Double, multiplier: Double) {
        var time = Math.PI / 3.0
        val stepCount = (radius * 3).roundToInt()
        val step = time * 2 / stepCount

        shockPointSet.clear()
        for (i in 0 until stepCount) {
            createPoint(time, radius, multiplier).draw(waveType)
            time -= step
        }
        shockWaves
            .filterKeys { it != uuid }
            .values.forEach {
                if(isCollide(it)) {
                    val sound = Sound.sound(SoundEvent.ITEM_SHIELD_BREAK, Sound.Source.PLAYER, 0.2f, 1f)
                    game.instance.playSound(sound, position)
                    game.instance.playSound(sound, it.position)

                    if(waveType == it.waveType) {
                        it.cancel()
                        cancel()
                    } else if(waveType == Type.CHARGED) {
                        it.cancel()
                    } else if(it.waveType == Type.CHARGED) {
                        cancel()
                    }
                }
            }
    }

    override fun createPoint(time: Double, radius: Double, multiplier: Double): ShockPoint {
        val x = radius * cos(time)
        val y = radius * sin(time)

        val offset = when(waveDirection) {
            Direction.VERTICAL -> Vec(x, y, time * multiplier)
            Direction.HORIZONTAL -> Vec(x, time * multiplier, y)
        }
        val shockPoint = ShockPoint(Util.rotate(position, direction, position.add(offset)))
        shockPointSet.add(shockPoint)
        return shockPoint
    }
}