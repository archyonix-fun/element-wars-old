package archyonix.ewars.features.mine_water

import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.mine_water.events.SuccessFishingEvent
import archyonix.ewars.game
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.shared.libs.Icons
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.FishingHookMeta
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent
import net.minestom.server.event.player.PlayerMoveEvent
import java.util.*

class MineWaterCrystalHandler : PublicEventNode("mine-water-crystal-handler") {
    private val random = Random()

    init {
        addListener(PlayerChangeHeldSlotEvent::class.java) { event ->
            if (game.stage != GameStage.RUNTIME) return@addListener
            game.instance.entities
                .filter { it.entityType == EntityType.FISHING_BOBBER }
                .find { (it.entityMeta as FishingHookMeta).ownerEntity == event.player }
                ?.remove()
        }
        addListener(PlayerMoveEvent::class.java) { event ->
            if (game.stage != GameStage.RUNTIME) return@addListener
            if (!event.player.itemInMainHand.hasGameItem()) return@addListener
            if (event.player.itemInMainHand.getGameItem()?.data?.getString("group") != "FISHING_ROD") return@addListener
            game.instance.entities
                .filter { it.entityType == EntityType.FISHING_BOBBER }
                .filter { it.position.distance(event.player.position) >= 32 }
                .find { (it.entityMeta as FishingHookMeta).ownerEntity == event.player }
                ?.remove()
        }
        addListener(SuccessFishingEvent::class.java) { event ->
            if (game.stage != GameStage.RUNTIME) return@addListener
            val storage = event.player.gameTeam().resources

            val itemStack = event.player.itemInMainHand
            if (!itemStack.hasGameItem()) return@addListener
            val gameItem = itemStack.getGameItem()!!
            if (gameItem.data?.getString("group", "none") != "FISHING_ROD") return@addListener

            val max = gameItem.data.getInt("maxFarm")
            val min = gameItem.data.getInt("minFarm")
            val count = min + random.nextInt(max - min) + 1
            if (count > 0) {
                storage.WATER_CRYSTAL += count
                event.player.showActionBar(
                    Component.join(
                        JoinConfiguration.spaces(),
                        Component.text("Получен кристалл воды x$count", Colors.TEXT),
                        Icons.WATER_CRYSTAL
                    )
                )
            }
        }
    }
}