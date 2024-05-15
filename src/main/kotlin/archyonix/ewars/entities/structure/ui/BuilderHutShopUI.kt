package archyonix.ewars.entities.structure.ui

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.shared.libs.UIs
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
import net.minestom.server.entity.Player

@UIPreset(path = "config/ui/structures/shop.yaml", isResource = false)
class BuilderHutShopUI(
    viewer: Player,
    structure: Structure
) : ShopUI(viewer, structure) {
    init {
        withDisplayName(UIs.BUILDER_HUT_SHOP)
    }
}