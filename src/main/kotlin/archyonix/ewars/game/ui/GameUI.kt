package archyonix.ewars.game.ui

import archyonix.ewars.shared.libs.Colors
import archyonix.uikit.UI
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

abstract class GameUI(viewer: Player, inventoryType: InventoryType) : UI(viewer, inventoryType) {
    init {
        Colors.colors().forEach { withColor(it, Colors.fromName(it)) }
    }
}