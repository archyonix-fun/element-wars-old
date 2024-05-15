package archyonix.ewars.entities.resource.handler

import archyonix.ewars.entities.item.enums.ItemFlag
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.game
import archyonix.ewars.shared.libs.Colors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import java.util.*

class ResourceHandler : PublicEventNode("resource-handler") {
    private val random = Random()

    init {
        addListener(PlayerBlockBreakEvent::class.java) { event ->
            val itemStack = event.player.itemInMainHand
            val gameItem = itemStack.asGameItem() ?: return@addListener
            val itemVariant = itemStack.asItemVariant() ?: return@addListener
            if (!itemVariant.flags.contains(ItemFlag.TOOL)) return@addListener
            event.isCancelled = true

            val resource = game.resourceManager.extract(event.blockPosition, gameItem.union ?: "none")
                ?: return@addListener

            val storage = event.player.gameTeam().resources
            val minFarm = (itemVariant.properties["minFarm"] ?: 0) as Int
            val maxFarm = (itemVariant.properties["maxFarm"] ?: 0) as Int

            val count = minFarm + random.nextInt(maxFarm - minFarm + 1)
            if (count <= 0) return@addListener

            storage.add(resource, count)
            event.player.showActionBar(
                Component.join(
                    JoinConfiguration.spaces(),
                    Component.text("Получен кристалл x$count", Colors.TEXT),
                    resource.getIcon()
                )
            )
        }
    }
}