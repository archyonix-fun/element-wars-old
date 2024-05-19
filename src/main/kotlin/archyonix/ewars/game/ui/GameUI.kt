package archyonix.ewars.game.ui

import archyonix.ewars.shared.libs.Colors
import archyonix.uikit.UI
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

abstract class GameUI(viewer: Player, inventoryType: InventoryType) : UI(viewer, inventoryType) {
    constructor(viewer: Player) : this(viewer, InventoryType.CHEST_6_ROW)

    init {
        Colors.colors().forEach { withColor(it, Colors.fromName(it)) }
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class RegisteredUI(
        val name: String
    )
}