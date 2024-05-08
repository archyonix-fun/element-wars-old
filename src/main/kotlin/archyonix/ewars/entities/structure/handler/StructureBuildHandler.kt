package archyonix.ewars.entities.structure.handler

import archyonix.ewars.entities.item.asStructureBuildItem
import archyonix.ewars.entities.item.hasStructureBuildItem
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.task.StructureAreaTask
import archyonix.ewars.game
import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.item.ItemStack

class StructureBuildHandler : PublicEventNode("structure-build-handler") {
    init {
        game.schedule.scheduleNextTick(StructureAreaTask())

        addListener(PlayerUseItemEvent::class.java) { event ->
            val player = event.player
            val itemStack = event.itemStack

            if (!itemStack.hasStructureBuildItem()) return@addListener
            val buildItem = itemStack.asStructureBuildItem()!!

            if (!player.isOnGround) {
                player.showActionBar("Вы должны стоять на земле", MessageType.WARNING)
                return@addListener
            }
            try {
                Structure(buildItem.config.name, player)
                showStartBuildMessage(player, buildItem.variant.displayName)
                if (player.itemInMainHand.amount() > 1) {
                    player.itemInMainHand = itemStack.withAmount(itemStack.amount() - 1)
                } else player.itemInMainHand = ItemStack.AIR
            } catch (e: Exception) {
                player.showActionBar(e.message!!, MessageType.WARNING)
            }
        }
    }

    private fun showStartBuildMessage(player: Player, structureName: String) {
        player.gameTeam()
            .showMessage("<text>Игрок <blue>${player.username}<text> начал строительство - <yellow>$structureName")
    }
}