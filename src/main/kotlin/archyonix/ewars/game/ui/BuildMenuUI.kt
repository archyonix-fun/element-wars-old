package archyonix.ewars.game.ui

import archyonix.ewars.entities.item.addStructureBuildTags
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.shared.libs.UIs
import archyonix.ewars.shared.libs.withColors
import archyonix.ewars.structureManager
import archyonix.uikit.UI
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
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

            val config = structureManager.getConfig(view.id)
            val variant = config.variants[0]

            val description = view.description as MutableList<String>
            description.addAll(variant.buildLore())

            view
                .withDescription(description)
                .onAction {
                    if (config.checkLimit(viewer.gameTeam())) {
                        if (variant.costInfo.pay(viewer)) {
                            viewer.inventory.addItemStack(getBuildItem(view.id))
                            showByMessage(viewer, variant.displayName)
                        } else viewer.showActionBar(
                            "У команды недостаточно кристаллов для покупки",
                            MessageType.WARNING
                        )
                    } else viewer.showActionBar("Превышен лимит на здание", MessageType.WARNING)
                }
        }
    }

    private fun showByMessage(player: Player, structureName: String) {
        player.gameTeam()
            .showMessage("<text>Игрок <blue>${player.username} <text>приобрел чертёж - <yellow>$structureName")
    }

    private fun getBuildItem(structureId: String): ItemStack {
        val config = structureManager.getConfig(structureId)
        val variant = config.variants[0]

        return ItemStack.of(Material.STICK)
            .withDisplayName(variant.displayName.withColors())
            .withLore(
                listOf(
                    "<blue>Используй ПКМ для размещения".withColors(),
                    "<yellow>Используй <red>Q <yellow>чтобы продать за половину стоимости".withColors()
                )
            )
            .addStructureBuildTags(structureId)
            .withTag(Tag.String("texture"), variant.name)
    }
}