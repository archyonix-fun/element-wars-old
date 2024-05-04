package archyonix.ewars.game.ui

import archyonix.ewars.entities.player.sendInfo
import archyonix.uikit.UI
import archyonix.uikit.components.UIButton
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

class TestUI(viewer: Player) : UI(viewer, InventoryType.CHEST_6_ROW) {
    init {
        withComponent(
            UIButton("button")
                .onAction {
                    viewer.sendInfo(it.id)
                }
        )
    }
}