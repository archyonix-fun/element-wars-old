package archyonix.ewars.entities.structure.handler

import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.structureManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

class StructureBuildHandler : PublicEventNode("structure-build-handler") {
    init {
        addListener(PlayerUseItemEvent::class.java) { event ->
            if (event.itemStack.material() != Material.STICK) return@addListener
            if (!event.itemStack.hasTag(Tag.String("structureId"))) return@addListener
            val player = event.player
            if (!player.isOnGround) {
                player.showActionBar("Вы должны стоять на земле", MessageType.ERROR)
                return@addListener
            }

            val itemStack = event.itemStack
            val structureId = itemStack.getTag(Tag.String("structureId"))
            val variantName = itemStack.getTag(Tag.String("variant"))
            val config = structureManager.getConfig(structureId)
            val variant = config.variants.find { it.name == variantName } ?: return@addListener
            try {
                Structure(structureId, player)
                if (player.itemInMainHand.amount() > 1) {
                    player.itemInMainHand = itemStack.withAmount(itemStack.amount() - 1)
                } else player.itemInMainHand = ItemStack.AIR

                player.gameTeam().sendMessage(
                    Component.join(
                        JoinConfiguration.spaces(),
                        Component.text("Игрок", Colors.TEXT),
                        Component.text(player.username, Colors.BLUE),
                        Component.text("начал строительство -", Colors.TEXT),
                        Component.text(variant.displayName, Colors.YELLOW),
                    )
                )
            } catch (e: Exception) {
                player.showActionBar(e.message!!, MessageType.WARNING)
            }
        }
    }
}