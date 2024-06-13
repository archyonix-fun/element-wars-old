package archyonix.ewars.features.pvp

import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.particle.Particle
import java.util.*

abstract class ShockWave(
    val position: Point,
    val direction: Float,
    val waveType: Type,
    val waveDirection: Direction,
    val radius: Double
) {
    companion object {
        val shockWaves: MutableMap<UUID, ShockWave> = HashMap()
    }

    val uuid: UUID = UUID.randomUUID()
    val shockPointSet: MutableSet<ShockPoint> = HashSet()
    val targets: MutableSet<Player> = HashSet()
    var isCancel: Boolean = false
    abstract fun spawn(callback: (targets: Set<Player>) -> Unit)
    abstract fun createPoint(time: Double, radius: Double): ShockPoint
    fun isCollide(wave: ShockWave): Boolean {
        val offset = radius * 2
        val minX = position.x() - offset
        val minY = position.y() - offset
        val minZ = position.z() - offset
        val maxX = position.x() + offset
        val maxY = position.y() + offset
        val maxZ = position.z() + offset

        val point = wave.position
        if (point.x() < minX || point.x() > maxX) return false
        if (point.y() < minY || point.y() > maxY) return false
        if (point.z() < minZ || point.z() > maxZ) return false

        shockPointSet.forEach { p1 ->
            wave.shockPointSet.forEach { p2 ->
                if (p1.isCollide(p2.position)) return true
            }
        }
        return false
    }

    fun cancel() {
        isCancel = true
        shockWaves.remove(uuid)
    }

    enum class Type { DEFAULT, CHARGED }
    enum class Direction { VERTICAL, HORIZONTAL }
    class ShockPoint(
        val position: Point
    ) {
        private val radius = 0.5
        fun isCollide(point: Point): Boolean {
            val minX = position.x() - radius
            val minY = position.y() - radius
            val minZ = position.z() - radius
            val maxX = position.x() + radius
            val maxY = position.y() + radius
            val maxZ = position.z() + radius

            if (point.x() < minX || point.x() > maxX) return false
            if (point.y() < minY || point.y() > maxY) return false
            return !(point.z() < minZ || point.z() > maxZ)
        }

        fun draw(waveType: Type) {
            val effect = if (waveType == Type.CHARGED) Particle.DRIPPING_WATER
            else Particle.CRIT
            game.getPlayers().forEach { Util.showEffect(it, position, effect) }
        }
    }
}