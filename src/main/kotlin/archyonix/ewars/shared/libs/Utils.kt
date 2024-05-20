package archyonix.ewars.shared.libs

import archyonix.ewars.entities.resource.Resource
import archyonix.ewars.entities.structure.Dependence
import archyonix.ewars.entities.structure.enums.DependenceType
import archyonix.ewars.features.shop.CostInfo
import archyonix.ewars.game
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.Times
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

fun String.toPos() = split(":")
    .map { it.toDouble() }
    .let {
        Pos(
            it[0],
            it[1],
            it[2],
            if (it.size >= 4) it[3].toFloat() else 0f,
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

fun Point.withDirection(dir: Direction, area: Short): Point {
    val width = area - 1.0

    val newX = when (dir) {
        Direction.NORTH -> abs(x() - width)
        Direction.EAST -> z()
        Direction.WEST -> abs(z() - width)
        else -> x()
    }
    val newZ = when (dir) {
        Direction.NORTH -> abs(z() - width)
        Direction.EAST -> abs(x() - width)
        Direction.WEST -> x()
        else -> z()
    }

    return Pos(newX, y(), newZ)
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

fun String.withDirection(direction: Direction): String {
    if (direction == Direction.SOUTH) return this

    var dir = ""
    dir += getDirection(Direction.SOUTH) ?: return this
    dir += getDirection(Direction.WEST) ?: return this
    dir += getDirection(Direction.NORTH) ?: return this
    dir += getDirection(Direction.EAST) ?: return this

    val newDir = when (direction) {
        Direction.WEST -> dir.moveChars(1)
        Direction.NORTH -> dir.moveChars(2)
        Direction.EAST -> dir.moveChars(3)
        else -> dir
    }

    var result = this
    result = result.replace("south=${dir[0] == '1'}", "south=${newDir[0] == '1'}")
    result = result.replace("west=${dir[1] == '1'}", "west=${newDir[1] == '1'}")
    result = result.replace("north=${dir[2] == '1'}", "north=${newDir[2] == '1'}")
    result = result.replace("east=${dir[3] == '1'}", "east=${newDir[3] == '1'}")
    return result
}

private fun String.moveChars(steps: Int): String {
    var result = this
    for (i in 1..steps) {
        val end = result.substring(result.length - 1, result.length)
        val other = result.substring(0, result.length - 1)
        result = end + other
    }
    return result
}

private fun String.getDirection(direction: Direction): String? {
    val name = direction.name.lowercase()
    return if (contains("$name=true")) "1"
    else if (contains("$name=false")) "0"
    else null
}

fun String.rotationWithDir(direction: Direction): String {
    if (!contains("rotation")) return this
    var rotation = 16
    while (rotation > 0) {
        if (this.contains("rotation=${--rotation}")) break
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

fun ConfigurationSection.toStringMap(): Map<String, String> {
    val data: MutableMap<String, String> = HashMap()
    for (key in getKeys(false)) {
        data[key] = getString(key)!!
    }
    return data
}

fun String.withColors(): Component {
    val components: MutableList<Component> = mutableListOf()
    for (line in split("<")) {
        var resultLine = line
        val cc = Colors.colors()
        var color: TextColor? = null
        for (colorName in Colors.colors()) {
            if (line.startsWith("$colorName>")) {
                resultLine = line.replace("$colorName>", "")
                color = Colors.fromName(colorName)
                break
            }
        }
        if (color == null) components.add(Component.text(resultLine))
        else components.add(Component.text(resultLine, color))
    }
    return Component
        .join(JoinConfiguration.noSeparators(), components)
        .decoration(TextDecoration.ITALIC, false)
}

fun String.withProperties(vararg properties: Pair<String, Any>): String {
    var result = this
    for (property in properties) {
        result = result.replace("%${property.first}%", property.second.toString())
    }
    return result
}

fun ConfigurationSection.loadDependencies(): List<Dependence> {
    return getStringList("dependencies")
        .map {
            val split = it.split(":")
            val depType = DependenceType.valueOf(split[0])
            val targets = split.subList(1, split.size).toSet()

            Dependence(depType, targets)
        }
}

fun ConfigurationSection.loadCost(): CostInfo {
    val section = getConfigurationSection("cost") ?: return CostInfo()
    val resources: MutableMap<Resource, Int> = EnumMap(Resource::class.java)
    for (key in section.getKeys(false)) {
        val resource = Resource.values().find { it.name == key } ?: continue
        resources[resource] = section.getInt(key)
    }
    return CostInfo(resources)
}