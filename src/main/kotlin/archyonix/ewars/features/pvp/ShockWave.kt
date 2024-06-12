package archyonix.ewars.features.pvp

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

abstract class ShockWave(
    val position: Point,
    val direction: Float,
    val waveType: Type,
    val waveDirection: Direction,
    private val radius: Double
) {
    private val shockPointSet: MutableSet<ShockPoint> = HashSet()
    private val multipliers = arrayOf(-2.0, -1.5, 1.5, 2.0)
    private val random: Random = Random()
    abstract fun spawn(callback: (targets: Set<Player>) -> Unit)
    fun createPoint(time: Double) {
        val x = radius * cos(time)
        val y = radius * sin(time)

        val offset = when(waveDirection) {
            Direction.VERTICAL -> Vec(x, y, time * multipliers[random.nextInt(multipliers.size)])
            Direction.HORIZONTAL -> Vec(x, time * multipliers[random.nextInt(multipliers.size)], y)
        }
        val shockPoint = ShockPoint(Util.rotate(position, direction, position.add(offset)))
        shockPointSet.add(shockPoint)
    }
    fun isCollide(wave: ShockWave): Boolean {
        val minX = position.x() - radius
        val minY = position.y() - radius
        val minZ = position.z() - radius
        val maxX = position.x() + radius
        val maxY = position.y() + radius
        val maxZ = position.z() + radius

        val point = wave.position
        if(point.x() < minX || point.x() > maxX) return false
        if(point.y() < minY || point.y() > maxY) return false
        if(point.z() < minZ || point.z() > maxZ) return false

        shockPointSet.forEach { p1 ->
            wave.shockPointSet.forEach { p2 ->
                if(p1.isCollide(p2.position)) return true
            }
        }
        return false
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

            if(point.x() < minX || point.x() > maxX) return false
            if(point.y() < minY || point.y() > maxY) return false
            return !(point.z() < minZ || point.z() > maxZ)
        }

        fun draw() {

        }
    }
}