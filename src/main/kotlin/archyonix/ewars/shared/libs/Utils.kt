package archyonix.ewars.shared.libs

import archyonix.ewars.game
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times
import net.minestom.server.coordinate.Pos
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

fun Pos.firstFromTop(): Pos {
    var y = 350
    while (y > 0) if (game.instance.getBlock(blockX(), y--, blockZ()).isSolid) break
    return Pos(x, y.toDouble(), z)
}

fun Pos.withDirection(dir: Direction, area: Short): Pos {
    val width = area - 1.0

    val newX = when (dir) {
        Direction.NORTH -> abs(x - width)
        Direction.EAST -> z
        Direction.WEST -> abs(z - width)
        else -> x
    }
    val newZ = when (dir) {
        Direction.NORTH -> abs(z - width)
        Direction.EAST -> abs(x - width)
        Direction.WEST -> x
        else -> z
    }

    return Pos(newX, y, newZ)
}

fun String.facingWithDir(direction: Direction): String {
    if (!contains("facing")) return this
    return when (direction) {
        Direction.NORTH -> {
            this.replace("facing=north", "facing==south")
                .replace("facing=south", "facing==north")
                .replace("facing=east", "facing==west")
                .replace("facing=west", "facing==east")
                .replace("==", "=")
        }

        Direction.WEST -> {
            this
                .replace("facing=north", "facing==east")
                .replace("facing=east", "facing==south")
                .replace("facing=south", "facing==west")
                .replace("facing=west", "facing==north")
                .replace("==", "=")
        }

        Direction.EAST -> {
            this
                .replace("facing=north", "facing==west")
                .replace("facing=west", "facing==south")
                .replace("facing=south", "facing==east")
                .replace("facing=east", "facing==north")
                .replace("==", "=")
        }

        else -> this
    }
}

fun String.rotationWithDir(direction: Direction): String {
    if (!contains("rotation")) return this
    var rotation = 16
    while (rotation > 0) {
        if(this.contains("rotation=${--rotation}")) break
    }
    rotation += 1
    val newRotation = when (direction) {
        Direction.EAST -> rotation + 12
        Direction.NORTH -> rotation + 8
        Direction.WEST -> rotation + 4
        else -> rotation
    }
    val _rotation = rotation - 1
    val _newRotation = (newRotation % 16) - 1
    return this.replace("rotation=$_rotation", "rotation=$_newRotation")
}