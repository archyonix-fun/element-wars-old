package archyonix.ewars.game.bars

import archyonix.ewars.entities.team.data.ResourceStorage
import archyonix.ewars.shared.libs.Icons
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import java.time.Duration

class TeamBar(private val resources: ResourceStorage) : GameBossBar(Duration.ofSeconds(1)) {
    override fun onRender(): Component {
        return Component.join(
            JoinConfiguration.separator(Component.text("   ")),
            InfoBlock(
                Icons.NATURAL_CRYSTAL,
                resources.NATURAL_CRYSTAL
            ).buildComponent(),
            InfoBlock(
                Icons.FLAME_CRYSTAL,
                resources.FLAME_CRYSTAL
            ).buildComponent(),
            InfoBlock(
                Icons.EARTH_CRYSTAL,
                resources.EARTH_CRYSTAL
            ).buildComponent(),
            InfoBlock(
                Icons.WATER_CRYSTAL,
                resources.WATER_CRYSTAL
            ).buildComponent(),
            LimitBlock(
                Icons.ENERGY,
                resources.ENERGY_USED,
                resources.ENERGY_LIMIT
            ).buildComponent(),
        )
    }

    class InfoBlock(
        private val icon: Component,
        private val value: Int
    ) {
        fun buildComponent(): Component {
            val prefix = "0".repeat(4 - value.toString().length)
            return Component.join(
                JoinConfiguration.noSeparators(),
                icon,
                Component.text(" $prefix", TextColor.fromHexString("#a5a5a5")),
                Component.text(value, TextColor.fromHexString("#ffffff"))
            )
        }
    }

    class LimitBlock(
        private val icon: Component,
        private val value: Int,
        private val maxValue: Int,
    ) {
        fun buildComponent(): Component {
            val prefix = "0".repeat(maxValue.toString().length - value.toString().length)
            return Component.join(
                JoinConfiguration.noSeparators(),
                icon,
                Component.text(" $prefix", TextColor.fromHexString("#a5a5a5")),
                Component.text(value, TextColor.fromHexString("#ffffff")),
                Component.text("/", TextColor.fromHexString("#a5a5a5")),
                Component.text(maxValue, TextColor.fromHexString("#ffffff")),
            )
        }
    }
}