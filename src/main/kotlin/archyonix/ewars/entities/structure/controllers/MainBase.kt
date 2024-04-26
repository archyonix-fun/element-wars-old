package archyonix.ewars.entities.structure.controllers

import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.shared.libs.CellPosition
import archyonix.ewars.shared.libs.getDirection
import net.minestom.server.entity.Player
import net.minestom.server.utils.Direction

class MainBase(
    teamId: String,
    pos: CellPosition,
    direction: Direction
) : Structure<MainBase.Variants>("main-base", teamId, pos, direction) {
    constructor(player: Player) : this(player._getTeam().id, CellPosition(player.position), player.getDirection())

    init {
        super.init()
        build(Variants.CAMP_FILE)
    }

    enum class Variants { CAMP_FILE, CAMP, FORTRESS }
}