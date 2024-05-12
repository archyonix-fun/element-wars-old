package archyonix.ewars.entities.structure.ui

import archyonix.ewars.entities.structure.controllers.StructureTower
import archyonix.ewars.entities.structure.controllers.normalizeDirection
import archyonix.ewars.entities.structure.enums.Wall
import archyonix.ewars.entities.structure.enums.WallType
import archyonix.ewars.game.ui.GameUI
import archyonix.ewars.shared.libs.UIs
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIView
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

@UIPreset(path = "config/ui/structures/menu/wall-menu.yaml", isResource = false)
class WallMenuUI(
    viewer: Player,
    private val structureTower: StructureTower,
    private val direction: Int,
) : GameUI(viewer, InventoryType.CHEST_6_ROW) {
    private val wallType = if (direction % 2 == 0) WallType.SLOPE else WallType.STRAIGHT
    private val current = structureTower.walls[direction.normalizeDirection()]

    init {
        withDisplayName(
            when (current) {
                Wall.WALL -> UIs.WALL_MENU
                Wall.GATE -> UIs.GATE_MENU
                else -> UIs.WALL_MENU
            }
        )
        initialize()
    }

    private fun initialize() {
        val gate = findComponent<UIView>("make-gate") ?: return
        val deconstruct = findComponent<UIView>("deconstruct") ?: return
        if (wallType == WallType.SLOPE || current == Wall.GATE) gate.withView(false)

        gate.onAction {
            structureTower.makeGate(direction)
            WallMenuUI(viewer, structureTower, direction).show()
        }

        deconstruct.onAction {
            structureTower.deconstructWall(direction, true)
            close()
        }
    }
}