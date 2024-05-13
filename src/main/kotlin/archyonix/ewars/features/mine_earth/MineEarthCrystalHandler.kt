package archyonix.ewars.features.mine_earth

import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.structure.Structure
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

class MineEarthCrystalHandler : PublicEventNode("mine-earth-crystal-handler") {
    private val random = Random()

    init {
        addListener(PlayerBlockBreakEvent::class.java) { event ->
            if (game.stage != GameStage.RUNTIME) return@addListener
            if (structureManager.findStructure<Structure>(event.blockPosition) != null) return@addListener
            val storage = event.player.gameTeam().resources

            val block = event.block
            if (block.name() != Block.IRON_ORE.name()) return@addListener

            val itemStack = event.player.itemInMainHand
            if (!itemStack.hasGameItem()) return@addListener
            val gameItem = itemStack.getGameItem()!!
            if (gameItem.data?.getString("group", "none") == "CONSTRUCTION_HAMMER") {
                event.isCancelled = true
                restoreIronOre(event.blockPosition)
                val count = random.nextInt(2) + 1
                storage.EARTH_CRYSTAL += count
                event.player.showActionBar(
                    Component.join(
                        JoinConfiguration.spaces(),
                        Component.text("Получен кристалл земли x$count", Colors.TEXT),
                        Icons.EARTH_CRYSTAL
                    )
                )
                return@addListener
            }

            if (gameItem.data?.getString("group", "none") != "PICKAXE") return@addListener

            event.isCancelled = true
            restoreIronOre(event.blockPosition)

            val max = gameItem.data.getInt("maxFarm")
            val min = gameItem.data.getInt("minFarm")
            val count = min + random.nextInt(max - min) + 1
            if (count > 0) {
                storage.EARTH_CRYSTAL += count
                event.player.showActionBar(
                    Component.join(
                        JoinConfiguration.spaces(),
                        Component.text("Получен кристалл земли x$count", Colors.TEXT),
                        Icons.EARTH_CRYSTAL
                    )
                )
            }
        }
    }
}

private fun restoreIronOre(position: Point) {
    game.instance.setBlock(position, Block.COBBLESTONE)
    MinecraftServer.getSchedulerManager().buildTask {
        game.instance.loadOptionalChunk(position).get()?.let {
            game.instance.setBlock(position, Block.IRON_ORE)
        }
    }.delay(Duration.ofMinutes(10)).schedule()
}