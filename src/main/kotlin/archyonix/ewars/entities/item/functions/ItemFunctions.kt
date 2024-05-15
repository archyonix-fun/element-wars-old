package archyonix.ewars.entities.item.functions

import archyonix.ewars.entities.item.GameItem
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

val GameItemIdTag = Tag.String("itemId")
val GameItemVariantTag = Tag.String("itemVariant")
val GameItemTextureTag = Tag.String("texture")
val GameItemRechargeTag = Tag.Float("recharge")

fun ItemStack.hasGameItem() = hasTag(GameItemIdTag)
fun ItemStack.hasGameItem(gameItem: GameItem): Boolean {
    if (!hasTag(GameItemIdTag)) return false
    return getTag(GameItemIdTag) == gameItem.id
}

fun ItemStack.asGameItem(): GameItem? {
    if (!hasTag(GameItemIdTag) || !hasTag(GameItemVariantTag)) return null
    val itemId = getTag(GameItemIdTag)
    return GameItem.items()
        .find { it.id == itemId }
}

fun ItemStack.asItemVariant(): GameItem.Variant? {
    if (!hasTag(GameItemIdTag) || !hasTag(GameItemVariantTag)) return null
    val itemId = getTag(GameItemIdTag)
    val variantId = getTag(GameItemVariantTag)
    return GameItem.items()
        .find { it.id == itemId }
        ?.getVariant(variantId)
}