package archyonix.ewars.entities.structure.ui

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.ui.GameUI
import archyonix.ewars.shared.libs.UIs
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

@UIPreset(path = "config/ui/structures/shop/mill-shop.yaml", isResource = false)
class MillShopUI(
    viewer: Player,
    structure: Structure
) : GameUI(viewer, InventoryType.CHEST_6_ROW) {
    init {
        withDisplayName(UIs.MILL_SHOP)

        findComponent<UIView>("deconstruct")
            ?.onAction {
                structure.deconstruct()
                close()
            }
    }
}