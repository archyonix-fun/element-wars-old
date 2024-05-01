package archyonix.ewars.game.functions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.entity.Player
import kotlin.math.floor

class ActionBar(
    vararg components: ActionBarComponent
) {
    private val components = components.map { it.buildComponent() }
    fun show(player: Player) {
        player.sendActionBar(
            Component.join(JoinConfiguration.spaces(), components)
        )
    }

    interface ActionBarComponent {
        fun buildComponent(): Component
    }

    class Progress(
        private val progress: Float
    ) : ActionBarComponent {
        private val size = 128
        private val color = when (progress) {
            in 0f..0.3f -> "#d42f2f"
            in 0.3f..0.7f -> "#d48e1e"
            else -> "#57cb47"
        }
        private val first = floor(size * progress).toInt()
        private val second = size - first

        override fun buildComponent() = Component.join(
            JoinConfiguration.noSeparators(),
            Component.text("|".repeat(first), TextColor.fromHexString(color)),
            Component.text("|".repeat(second), TextColor.fromHexString("#333333"))
        )
    }

    class IconText(
        private val icon: Component,
        private val text: String
    ) : ActionBarComponent {
        override fun buildComponent(): Component =
            Component.join(
                JoinConfiguration.spaces(), icon,
                Component.text(text, TextColor.fromHexString("#888888"))
            )
    }
}