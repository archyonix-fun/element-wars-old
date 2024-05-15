package archyonix.ewars.entities.structure.ui

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.game.ui.GameUI
import archyonix.ewars.shared.libs.UIs
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

@UIPreset(path = "config/ui/structures/shop.yaml", isResource = false)
class CraftsmanHouseShopUI(
    viewer: Player,
    structure: Structure
) : ShopUI(viewer, structure) {
    init {
        withDisplayName(UIs.CRAFTSMAN_HOUSE_SHOP)
    }
}