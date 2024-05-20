package archyonix.ewars.game.ui.registered

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.enums.ItemVariant
import archyonix.ewars.entities.message.Message
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.game.ui.GameUI
import archyonix.ewars.shared.libs.UIs
import archyonix.ewars.shared.libs.withColors
import archyonix.ewars.shared.libs.withProperties
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

@ClassLibrary.RegisteredClass("BUILD_MENU", ClassLibrary.Type.UI)
@UIPreset(path = "config/ui/build-menu.yaml", isResource = false)
class BuildMenuUI(viewer: Player) : GameUI(viewer, InventoryType.CHEST_6_ROW) {
    private val team = viewer.gameTeam()

    init {
        withDisplayName(UIs.BUILD_MENU)
        for (component in components) {
            val view = component as UIView

            val config = game.structureManager.getConfig(view.id)
            val variant = config.variants[0]

            val description = view.description as MutableList<String>
            //description.addAll(variant.buildLore(team))

            view
                .withDescription(description)
                .onRender {
                    if (!variant.checkDependencies(team)) it.tags["texture"] = "HIDDEN"
                    it
                }
                .onAction {
                    if(!config.checkBuildingLimit(viewer.gameTeam())) {
                        viewer.showActionBar(Message.BUILDING_LIMIT_EXCEEDED, MessageType.WARNING)
                        return@onAction
                    }
                    if(!variant.checkDependencies(team)) {
                        viewer.showActionBar(Message.NO_DEPENDENCIES, MessageType.WARNING)
                        return@onAction
                    }
                    if (variant.cost.pay(viewer)) {
                        viewer.inventory.addItemStack(getBuildItem(view.id))
                        showByMessage(viewer, variant.name)
                    } else viewer.showActionBar(Message.NOT_ENOUGH_MONEY, MessageType.WARNING)
                }
        }
    }

    private fun showByMessage(player: Player, structureName: String) {
        player.gameTeam().showMessage(
            Message.PLAYER_BOUGHT_THE_BUILDING
                .withProperties(
                    Pair("username", player.username),
                    Pair("structureName", structureName)
                )
        )
    }

    private fun getBuildItem(structureId: String): ItemStack {
        val config = game.structureManager.getConfig(structureId)
        val variant = config.variants[0]

        return GameItem.STRUCTURE_SCHEMA
            .getVariant(ItemVariant.DEFAULT)!!
            .createItemStack(1)
            .withDisplayName(variant.name.withColors())
            .withTag(Tag.String("structureId"), structureId)
            .withTag(Tag.String("texture"), variant.id)
    }
}