package archyonix.ewars.features.mine_natural

import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.player.sendActionBarInfo
import archyonix.ewars.game
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.shared.libs.Icons
import archyonix.ewars.structureManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.instance.block.Block
import java.util.*

class MineNaturalCrystalHandler : PublicEventNode("mine-natural-crystal-handler") {
    private val random = Random()

    init {
        addListener(PlayerBlockBreakEvent::class.java) { event ->
            if (game.stage != GameStage.RUNTIME) return@addListener
            if (structureManager.findStructure(event.blockPosition) != null) return@addListener
            val storage = event.player._getTeam().resources

            when (event.block.name()) {
                Block.BIRCH_LEAVES.name(),
                Block.SPRUCE_LEAVES.name() -> {
                    event.isCancelled = true
                    event.instance.setBlock(event.blockPosition, Block.AIR)
                    val count = if (random.nextFloat() > .8f) random.nextInt(3) else 0
                    if (count > 0) {
                        storage.NATURAL_CRYSTAL += count
                        event.player.sendActionBarInfo(
                            Component.join(
                                JoinConfiguration.spaces(),
                                Component.text("Получен природный кристалл x$count", Colors.TEXT),
                                Icons.NATURAL_CRYSTAL
                            )
                        )
                    }
                }

                Block.BIRCH_LOG.name(),
                Block.SPRUCE_LOG.name() -> {
                    val itemStack = event.player.itemInMainHand
                    if (!itemStack.hasGameItem()) return@addListener
                    val gameItem = itemStack.getGameItem()!!
                    if (gameItem.data?.getString("group", "none") != "AXE") return@addListener

                    event.isCancelled = true
                    breakTree(event.blockPosition)

                    val max = gameItem.data.getInt("maxFarm")
                    val min = gameItem.data.getInt("minFarm")
                    val count = min + random.nextInt(max - min) + 1
                    if (count > 0) {
                        storage.NATURAL_CRYSTAL += count
                        event.player.sendActionBarInfo(
                            Component.join(
                                JoinConfiguration.spaces(),
                                Component.text("Получен природный кристалл x$count", Colors.TEXT),
                                Icons.NATURAL_CRYSTAL
                            )
                        )
                    }
                }

                else -> return@addListener
            }
        }
    }
}