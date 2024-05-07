package archyonix.ewars.game.task

import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.game
import archyonix.ewars.shared.libs.Colors
import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import java.time.Duration

class HiddenTask : Runnable {
    private val validBlocks = setOf(
        Block.BROWN_MUSHROOM.name(),
        Block.SUGAR_CANE.name(),
        Block.SHORT_GRASS.name()
    )

    override fun run() {
        for (player in game.getPlayers()) {
            if (player.isSneaking && validBlocks.contains(player.instance.getBlock(player.position).name())) {
                player.isInvisible = true
                player.showActionBar(Component.text("Вы затаились", Colors.TEXT))
                continue
            }
            player.isInvisible = false
        }

        game.schedule.buildTask(this).delay(Duration.ofMillis(1000)).schedule()
    }
}