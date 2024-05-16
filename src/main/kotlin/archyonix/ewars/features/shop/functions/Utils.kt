package archyonix.ewars.features.shop.functions

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.features.shop.ShopItem
import archyonix.uikit.components.UIView
import net.minestom.server.item.Material

fun UIView.withGameItem(item: GameItem.Variant): UIView {
    if(item.name != null) withDisplayName(item.name)
    if(item.material != null) withIcon(item.material.name())
    if(item.texture != null) withTag("texture", item.texture)

    return withDescription(item.description)
}

fun UIView.withShopIcon(icon: ShopItem.ShopIcon): UIView {
    withIcon(Material.STICK.name())
    if(icon.texture != null) withTag("texture", icon.texture)
    if(icon.name != null) withDisplayName(icon.name)

    return withDescription(icon.description)
}