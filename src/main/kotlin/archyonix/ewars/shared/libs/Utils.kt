package archyonix.ewars.shared.libs

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times
import net.minestom.server.coordinate.Pos
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
        Direction.SOUTH -> z
        Direction.NORTH -> abs(z - width)
        else -> x
    }
    val newZ = when(dir) {
        Direction.WEST -> abs(z - width)
        Direction.SOUTH -> x
        Direction.NORTH -> abs(x - width)
        else -> z
    }

    return Pos(newX, y, newZ)
}