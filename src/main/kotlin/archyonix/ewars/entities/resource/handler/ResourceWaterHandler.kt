package archyonix.ewars.entities.resource.handler

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.resource.events.SuccessFishingEvent
import archyonix.ewars.entities.resource.task.FishingTask
import archyonix.ewars.game
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

class ResourceWaterHandler : PublicEventNode("resource-water-handler") {
    private val random = Random()

    init {
        game.schedule.scheduleNextTick(FishingTask())

        addListener(PlayerChangeHeldSlotEvent::class.java) { event ->
            game.instance.entities
                .filter { it.entityType == EntityType.FISHING_BOBBER }
                .find { (it.entityMeta as FishingHookMeta).ownerEntity == event.player }
                ?.remove()
        }
        addListener(PlayerMoveEvent::class.java) { event ->
            if (!event.player.itemInMainHand.hasGameItem(GameItem.FISHING_ROD)) return@addListener
            game.instance.entities
                .filter { it.entityType == EntityType.FISHING_BOBBER }
                .filter { it.position.distance(event.player.position) >= 32 }
                .find { (it.entityMeta as FishingHookMeta).ownerEntity == event.player }
                ?.remove()
        }
        addListener(SuccessFishingEvent::class.java) { event ->
            val itemStack = event.player.itemInMainHand
            if (!itemStack.hasGameItem(GameItem.FISHING_ROD)) return@addListener
            val fishingRod = itemStack.asItemVariant()!!

            val storage = event.player.gameTeam().resources
            val minFarm = (fishingRod.properties["minFarm"] ?: 0) as Int
            val maxFarm = (fishingRod.properties["maxFarm"] ?: 0) as Int

            val count = minFarm + random.nextInt(maxFarm - minFarm + 1)
            if (count <= 0) return@addListener

            storage.WATER_CRYSTAL += count
            event.player.showActionBar(
                Component.join(
                    JoinConfiguration.spaces(),
                    Component.text("Получен кристалл x$count", Colors.TEXT),
                    Icons.WATER_CRYSTAL
                )
            )
        }
    }
}