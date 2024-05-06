package archyonix.ewars.game.ui

import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.player.sendWarning
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.shared.libs.UIs
import archyonix.ewars.structureManager
import archyonix.uikit.UI
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

@UIPreset(path = "config/ui/build-menu-t1.yaml", isResource = false)
class BuildMenuUI(viewer: Player) : UI(viewer, InventoryType.CHEST_6_ROW) {
    init {
        withDisplayName(UIs.BUILD_MENU)
        for (component in components) {
            val view = component as UIView

            val structure = structureManager.getConfig(view.id)
            val variantName = view.tags["variant"] ?: continue
            val variant = structure.variants.find { it.name == variantName } ?: continue
            val team = viewer._getTeam()

            val description = view.description as MutableList<String>
            description.addAll(variant.buildLore())

            view
                .withDescription(description)
                .onAction {
                    if (structure.checkLimit(viewer._getTeam())) {
                        if (variant.costInfo.pay(viewer)) {
                            team.sendMessage(
                                Component.join(
                                    JoinConfiguration.spaces(),
                                    Component.text("Игрок", Colors.TEXT),
                                    Component.text(viewer.username, Colors.BLUE),
                                    Component.text("приобрел строение -", Colors.TEXT),
                                    Component.text(variant.displayName, Colors.YELLOW),
                                )
                            )
                        } else viewer.sendWarning("У команды недостаточно кристаллов для покупки")
                    } else viewer.sendWarning("Превышен лимит на здание")
                }
        }
    }
}