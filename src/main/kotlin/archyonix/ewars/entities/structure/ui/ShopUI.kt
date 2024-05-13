package archyonix.ewars.entities.structure.ui

import archyonix.ewars.entities.item.functions.getGameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.message.Message
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.player.showMessage
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.features.shop.ShopItem
import archyonix.ewars.game.ui.GameUI
import archyonix.ewars.shared.libs.withProperties
import archyonix.uikit.components.UIList
import archyonix.uikit.components.UIView
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack

abstract class ShopUI(
    viewer: Player,
    structure: Structure
) : GameUI(viewer, InventoryType.CHEST_6_ROW) {
    val team = structure.team
    val shopItems = structure.config.shopItems()
    val shopList = findComponent<UIList>("shopList")!!

    init {
        for (item in shopItems) {
            shopList.withComponent(buildItem(item))
        }
    }

    private fun buildItem(item: ShopItem): UIView {
        val gameItem = item.gameItem
        val lore: MutableList<String> = gameItem.lore
        lore.addAll(item.buildLore(team))

        return UIView(gameItem.id)
            .withIcon(gameItem.material.name())
            .withAmount(item.amount)
            .withDisplayName(gameItem.displayName)
            .withDescription(lore)
            .withTag("texture", gameItem.texture)
            .onRender {
                if (!item.checkDependencies(team)) it.tags["texture"] = "HIDDEN"
                it
            }
            .onAction {
                if (item.checkDependencies(team)) {
                    if (item.costInfo.pay(viewer)) {
                        val group = gameItem.data?.getString("group")
                        if (group != null) {
                            for (slot in 0..viewer.inventory.size) {
                                val itemStack = viewer.inventory.getItemStack(slot)
                                if (!itemStack.hasGameItem()) continue
                                val itemGroup = itemStack.getGameItem()?.data?.getString("group") ?: continue
                                if (itemGroup == group) {
                                    viewer.inventory.setItemStack(slot, ItemStack.AIR)
                                    break
                                }
                            }
                        }
                        viewer.inventory.addItemStack(gameItem.createItemStack(item.amount))
                        showByMessage(viewer, gameItem.displayName, item.amount)
                    } else viewer.showActionBar(Message.NOT_ENOUGH_MONEY, MessageType.WARNING)
                } else viewer.showActionBar(Message.NO_DEPENDENCIES, MessageType.WARNING)
            }
    }

    private fun showByMessage(player: Player, itemName: String, amount: Int) {
        player.showMessage(
            Message.PLAYER_BY_ITEM
                .withProperties(
                    Pair("itemName", itemName),
                    Pair("amount", amount)
                )
        )
    }
}