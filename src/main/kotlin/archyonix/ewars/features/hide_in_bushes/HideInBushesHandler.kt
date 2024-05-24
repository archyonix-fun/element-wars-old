package archyonix.ewars.features.hide_in_bushes

import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.game
import archyonix.ewars.game.events.GameTickEvent
import archyonix.ewars.shared.libs.Colors
import net.kyori.adventure.text.Component
import net.minestom.server.event.PublicEventNode
import net.minestom.server.instance.block.Block

class HideInBushesHandler: PublicEventNode("hide in bushes") {
    private val validBlocks = setOf(Block.BROWN_MUSHROOM.name(), Block.SUGAR_CANE.name(), Block.SHORT_GRASS.name())
    init {
        addListener(GameTickEvent::class.java) {
            for (player in game.getPlayers()) {
                if (player.isSneaking && validBlocks.contains(player.instance.getBlock(player.position).name())) {
                    player.isInvisible = true
                    player.showActionBar(Component.text("Вы затаились", Colors.TEXT))
                    continue
                }
                player.isInvisible = false
            }
        }
    }
}