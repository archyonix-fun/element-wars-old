package archyonix.ewars.game.bars

import archyonix.ewars.entities.team.data.ResourceStorage
import archyonix.ewars.game
import archyonix.ewars.shared.libs.Icons
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

class TeamBar(private val resources: ResourceStorage) : GameBossBar(Duration.ofSeconds(1)) {
    private val format = SimpleDateFormat("HH:mm:ss")
    override fun onRender(): Component {
        return Component.join(
            JoinConfiguration.separator(Component.text("   ")),
            IntInfoBlock(
                Icons.NATURAL_CRYSTAL,
                resources.NATURAL_CRYSTAL
            ).buildComponent(),
            IntInfoBlock(
                Icons.FLAME_CRYSTAL,
                resources.FLAME_CRYSTAL
            ).buildComponent(),
            IntInfoBlock(
                Icons.EARTH_CRYSTAL,
                resources.EARTH_CRYSTAL
            ).buildComponent(),
            IntInfoBlock(
                Icons.WATER_CRYSTAL,
                resources.WATER_CRYSTAL
            ).buildComponent(),
            LimitBlock(
                Icons.ENERGY,
                resources.ENERGY_USED,
                resources.ENERGY_LIMIT
            ).buildComponent(),
            InfoBlock(
                Icons.SAND_CLOCK,
                calcTime()
            ).buildComponent(),
        )
    }

    private fun calcTime(): String {
        val current = Date(Date().time - game.startTime)
        return format.format(current)
    }

    class IntInfoBlock(
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

    class InfoBlock(
        private val icon: Component,
        private val value: String
    ) {
        fun buildComponent(): Component {
            return Component.join(
                JoinConfiguration.spaces(),
                icon,
                Component.text(value, TextColor.fromHexString("#ffffff")),
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