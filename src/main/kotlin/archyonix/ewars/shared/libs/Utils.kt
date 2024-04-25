package archyonix.ewars.shared.libs

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.utils.Direction
import java.time.Duration
import kotlin.math.abs

fun String.toPos() = split(":")
    .map { it.toDouble() }
    .let {
        Pos(
            it[0],
            it[1],
            it[2],
            if (it.size == 4) it[3].toFloat() else 0f,
            if (it.size == 5) it[4].toFloat() else 0f,
        )
    }

fun createTitle(title: String = "", subtitle: String = "", delay: Long = 1000) =
    Title.title(
        Component.text(title),
        Component.text(subtitle),
        Times.times(Duration.ZERO, Duration.ofMillis(delay), Duration.ZERO)
    )

fun Pos.withDirection(dir: Direction, area: Short): Pos {
    val width = area -1.0

    val newX = when(dir) {
        Direction.WEST -> abs(x - width)
        Direction.NORTH -> z
        Direction.SOUTH -> abs(z - width)
        else -> x
    }
    val newZ = when(dir) {
        Direction.WEST -> abs(z - width)
        Direction.NORTH -> x
        Direction.SOUTH -> abs(x - width)
        else -> z
    }

    return Pos(newX, y, newZ)
}

fun Player.getDirection(): Direction {
    val yaw = position.yaw
    return when {
        yaw > -135 && yaw <= -45 -> Direction.EAST
        yaw > -45 && yaw <= 45 -> Direction.SOUTH
        yaw > 45 && yaw <= 135 -> Direction.WEST
        yaw > 135 || yaw <= -135 -> Direction.NORTH
        else -> Direction.SOUTH
    }
}