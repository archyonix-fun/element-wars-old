package archyonix.ewars.entities.structure.ui

import archyonix.ewars.entities.item.enums.ItemVariant
import archyonix.ewars.entities.item.functions.asItemVariant
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
import net.minestom.server.item.Material

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

        findComponent<UIView>("deconstruct")
            ?.onAction {
                structure.deconstruct()
                close()
            }
    }

    private fun buildItem(item: ShopItem): UIView {
        val variant = item.gameItem
        val lore: MutableList<String> = ArrayList(variant.description)
        lore.addAll(item.buildLore(team))

        return UIView("${variant.item.id}-${variant.id}")
            .onRender {
                it
                    .withDisplayName(variant.name ?: "")
                    .withDescription(lore)
                    .withAmount(item.amount)
                    .withIcon(variant.material?.name() ?: Material.AIR.name())
                    .withTag("texture", variant.texture ?: "")
                if (!item.checkDependencies(team)) it.tags["texture"] = "HIDDEN"
                it
            }
            .onAction {
                if (item.checkDependencies(team)) {
                    val union = variant.item.union
                    val order = ItemVariant.valueOf(variant.id).order
                    if (union != null && viewer.inventory.itemStacks
                            .filter { it.hasGameItem(variant.item) }
                            .find { ItemVariant.valueOf(it.asItemVariant()!!.id).order >= order } != null
                    ) {
                        viewer.showActionBar(Message.ALREADY_PURCHASED, MessageType.WARNING)
                        return@onAction
                    }
                    if (item.costInfo.pay(viewer)) {
                        if (union != null) {
                            for (slot in 0 until viewer.inventory.size) {
                                val itemStack = viewer.inventory.getItemStack(slot)
                                if (!itemStack.hasGameItem(variant.item)) continue
                                viewer.inventory.setItemStack(slot, ItemStack.AIR)
                                break
                            }
                        }
                        viewer.inventory.addItemStack(variant.createItemStack(item.amount))
                        showByMessage(viewer, variant.name ?: "", item.amount)
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