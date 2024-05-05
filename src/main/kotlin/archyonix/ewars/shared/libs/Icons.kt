package archyonix.ewars.shared.libs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

class Icons {
    companion object {
        val NATURAL_CRYSTAL = Component.text("\uE000", TextColor.fromHexString("#ffffff"))
        val FLAME_CRYSTAL = Component.text("\uE001", TextColor.fromHexString("#ffffff"))
        val EARTH_CRYSTAL = Component.text("\uE002", TextColor.fromHexString("#ffffff"))
        val WATER_CRYSTAL = Component.text("\uE003", TextColor.fromHexString("#ffffff"))
        val ENERGY = Component.text("\uE004", TextColor.fromHexString("#ffffff"))

        val HAMMER = Component.text("\uE008", TextColor.fromHexString("#ffffff"))
        val HEART = Component.text("\uE009", TextColor.fromHexString("#ffffff"))
        val SHIELD = Component.text("\uE010", TextColor.fromHexString("#ffffff"))
        val ACCEPT = Component.text("\uE011", TextColor.fromHexString("#ffffff"))
        val DENY = Component.text("\uE012", TextColor.fromHexString("#ffffff"))
    }
}