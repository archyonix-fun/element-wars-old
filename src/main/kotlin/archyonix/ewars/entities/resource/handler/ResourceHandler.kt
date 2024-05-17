package archyonix.ewars.entities.resource.handler

import archyonix.ewars.entities.item.enums.ItemFlag
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.structureManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import java.util.*
import kotlin.math.round

class ResourceHandler : PublicEventNode("resource-handler") {
    private val random = Random()

    init {
        addListener(PlayerBlockBreakEvent::class.java) { event ->
            if (structureManager.findStructure<Structure>(event.blockPosition) != null) return@addListener
            val itemStack = event.player.itemInMainHand
            val gameItem = itemStack.asGameItem() ?: return@addListener
            val itemVariant = itemStack.asItemVariant() ?: return@addListener
            if (!itemVariant.flags.contains(ItemFlag.TOOL)) return@addListener
            event.isCancelled = true

            val resourceSet = game.resourceManager.extract(event.blockPosition, gameItem.union ?: "none")
                ?: return@addListener

            val storage = event.player.gameTeam().resources
            val multiplier = (itemVariant.properties["extractorMultiplier"] ?: "0.0").toString().toFloat()

            for (volume in resourceSet) {
                val minFarm = round(volume.min * multiplier).toInt()
                val maxFarm = round(volume.max * multiplier).toInt()
                val count = minFarm + random.nextInt(maxFarm - minFarm + 1)
                if (count <= 0) continue

                storage.add(volume.resource, count)
                event.player.showActionBar(
                    Component.join(
                        JoinConfiguration.spaces(),
                        Component.text("Получен кристалл x$count", Colors.TEXT),
                        volume.resource.getIcon()
                    )
                )
            }
        }
    }
}