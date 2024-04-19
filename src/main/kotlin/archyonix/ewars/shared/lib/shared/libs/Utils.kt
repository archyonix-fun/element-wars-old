package archyonix.ewars.shared.lib.shared.libs

import net.minestom.server.coordinate.Pos

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