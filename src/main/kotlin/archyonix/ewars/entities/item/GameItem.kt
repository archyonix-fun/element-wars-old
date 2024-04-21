package archyonix.ewars.entities.item

import archyonix.ewars.entities.item.functions.GameItemIdTag
import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

class GameItem(
    val id: String,
    val displayName: String,
    val lore: List<String>,
    material: String
) {
    val material: Material = Material.fromNamespaceId(material)!!
    fun buildItemStack(amount: Int = 1): ItemStack {
        return ItemStack.of(material, amount)
            .withDisplayName(Component.text(displayName))
            .withLore(lore.map { Component.text(it) })
            .withTag(GameItemIdTag, id)
    }
}