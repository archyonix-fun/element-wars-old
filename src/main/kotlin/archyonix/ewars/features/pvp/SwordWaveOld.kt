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

class SwordWaveOld(
    private val origin: Point,
    private val direction: Float,
    private val type: Type,
    private val startRadius: Double,
    private val endRadius: Double,
    private val step: Double
) {
    companion object {
        private val waveReg: MutableMap<UUID, SwordWaveOld> = HashMap()
    }

    private val uuid: UUID = UUID.randomUUID()
    private val arcPoints: MutableSet<Point> = HashSet()
    private var isCancel: Boolean = false

    private var targets: MutableSet<Player> = HashSet()
    private val multiplier = when (Random().nextInt(3)) {
        0 -> 1.5
        2 -> -1.5
        else -> 0.0
    }

    fun run(callback: (targets: Set<Player>) -> Unit) {
        waveReg[uuid] = this
        var radius = startRadius
        game.schedule.submitTask {
            if (radius <= endRadius && !isCancel) {
                drawArc(radius)
                radius += step
                return@submitTask TaskSchedule.millis(25)
            } else callback(targets)
            waveReg.remove(uuid)
            return@submitTask null
        }
    }

    private fun drawArc(radius: Double) {
        var time = Math.PI / 3.0

        val stepCount = (radius * 3).roundToInt()
        val step = time * 2 / stepCount

        arcPoints.clear()
        for (i in 0 until stepCount) {
            drawPoint(time, radius)
            time -= step
        }

        waveReg.values.forEach { value ->
            if (value.uuid != uuid)
                for (point in arcPoints) {
                    for (element in value.arcPoints) {
                        if (element.distance(point) <= 0.5) {
                            val sound = Sound.sound(SoundEvent.BLOCK_ANVIL_PLACE, Sound.Source.BLOCK, 0.1f, 1f)
                            game.getPlayers()
                                .filter { point.distance(it.position) <= 8 }
                                .forEach { it.playSound(sound) }

                            value.isCancel = true
                            isCancel = true

                            return
                        }
                    }
                }
        }
    }

    private fun drawPoint(time: Double, radius: Double) {
        val x = radius * cos(time)
        val y = radius * sin(time)

        val offset = if (type == Type.VERTICAL) Vec(x, y, time * multiplier)
        else Vec(x, time * multiplier, y)
        val point = Util.rotate(origin, direction, origin.add(offset))
        val colliderPoint = point.sub(0.0, 1.0, 0.0)
        arcPoints.add(point)

        game.getPlayers()
            .filter { it.position.distance(origin) < 32 }
            .map {
                Util.showEffect(it, point)
                it
            }
            .filter { !targets.contains(it) }
            .forEach {
                if (it.position.distance(colliderPoint) <= 0.5)
                    targets.add(it)
            }
    }

    enum class Type { VERTICAL, HORIZONTAL }
}