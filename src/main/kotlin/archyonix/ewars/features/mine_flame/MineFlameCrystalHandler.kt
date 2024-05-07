package archyonix.ewars.features.mine_flame

import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.game
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.shared.libs.Icons
import archyonix.ewars.structureManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.instance.block.Block
import java.time.Duration
import java.util.*

class MineFlameCrystalHandler : PublicEventNode("mine-flame-crystal-handler") {
    private val random = Random()

    init {
        addListener(PlayerBlockBreakEvent::class.java) { event ->
            if (game.stage != GameStage.RUNTIME) return@addListener
            if (structureManager.findStructure(event.blockPosition) != null) return@addListener
            val storage = event.player.gameTeam().resources

            val block = event.block
            if (block.name() != Block.MAGMA_BLOCK.name()) return@addListener

            val itemStack = event.player.itemInMainHand
            if (!itemStack.hasGameItem()) return@addListener
            val gameItem = itemStack.getGameItem()!!
            if (gameItem.data?.getString("group", "none") != "PICKAXE") return@addListener

            event.isCancelled = true
            restoreMagmaBlock(event.blockPosition)

            val max = gameItem.data.getInt("maxFarm")
            val min = gameItem.data.getInt("minFarm")
            val count = min + random.nextInt(max - min) + 1
            if (count > 0) {
                storage.FLAME_CRYSTAL += count
                event.player.showActionBar(
                    Component.join(
                        JoinConfiguration.spaces(),
                        Component.text("Получен кристалл пламени x$count", Colors.TEXT),
                        Icons.FLAME_CRYSTAL
                    )
                )
            }
        }
    }
}

private fun restoreMagmaBlock(position: Point) {
    game.instance.setBlock(position, Block.OBSIDIAN)
    MinecraftServer.getSchedulerManager().buildTask {
        game.instance.loadOptionalChunk(position).get()?.let {
            game.instance.setBlock(position, Block.MAGMA_BLOCK)
        }
    }.delay(Duration.ofMinutes(10)).schedule()
}