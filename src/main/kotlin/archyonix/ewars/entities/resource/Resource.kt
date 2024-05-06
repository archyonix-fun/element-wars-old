package archyonix.ewars.entities.resource

import archyonix.ewars.shared.libs.Icons
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

enum class Resource {
    NATURAL_CRYSTAL,
    FLAME_CRYSTAL,
    EARTH_CRYSTAL,
    WATER_CRYSTAL;

    fun getIcon(): TextComponent = when (name) {
        "NATURAL_CRYSTAL" -> Icons.NATURAL_CRYSTAL
        "FLAME_CRYSTAL" -> Icons.FLAME_CRYSTAL
        "EARTH_CRYSTAL" -> Icons.EARTH_CRYSTAL
        "WATER_CRYSTAL" -> Icons.WATER_CRYSTAL
        else -> Component.empty()
    }
}