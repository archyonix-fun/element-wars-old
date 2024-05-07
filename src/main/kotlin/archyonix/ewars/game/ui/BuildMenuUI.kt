package archyonix.ewars.game.ui

import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showMessage
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.shared.libs.UIs
import archyonix.ewars.structureManager
import archyonix.uikit.UI
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
import archyonix.uikit.components.withColors
import archyonix.uikit.components.withProperties
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

@UIPreset(path = "config/ui/build-menu-t1.yaml", isResource = false)
class BuildMenuUI(viewer: Player) : UI(viewer, InventoryType.CHEST_6_ROW) {
    init {
        withDisplayName(UIs.BUILD_MENU)
        for (component in components) {
            val view = component as UIView

            val structure = structureManager.getConfig(view.id)
            val variantName = view.tags["variant"] ?: continue
            val variant = structure.variants.find { it.name == variantName } ?: continue
            val team = viewer.gameTeam()

            val description = view.description as MutableList<String>
            description.addAll(variant.buildLore())

            view
                .withDescription(description)
                .onAction {
                    if (structure.checkLimit(viewer.gameTeam())) {
                        if (variant.costInfo.pay(viewer)) {
                            viewer.inventory.addItemStack(getBuildItem(it))
                            team.sendMessage(
                                Component.join(
                                    JoinConfiguration.spaces(),
                                    Component.text("Игрок", Colors.TEXT),
                                    Component.text(viewer.username, Colors.BLUE),
                                    Component.text("приобрел -", Colors.TEXT),
                                    Component.text(variant.displayName, Colors.YELLOW),
                                )
                            )
                        } else viewer.showMessage("У команды недостаточно кристаллов для покупки", MessageType.WARNING)
                    } else viewer.showMessage("Превышен лимит на здание", MessageType.WARNING)
                }
        }
    }

    private fun getBuildItem(view: UIView): ItemStack {
        var stack = ItemStack.of(Material.STICK)
            .withDisplayName(
                view.displayName
                    .withProperties(view.properties)
                    .withColors(colors)
            )
            .withLore(
                listOf(
                    Component
                        .text("<Используй ПКМ для размещения>", Colors.BLUE)
                        .decoration(TextDecoration.ITALIC, false)
                )
            )
            .withTag(Tag.String("structureId"), view.id)
        view.tags.map { stack = stack.withTag(Tag.String(it.key), it.value) }
        return stack
    }
}