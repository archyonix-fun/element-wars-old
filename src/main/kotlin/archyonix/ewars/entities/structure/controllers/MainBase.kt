package archyonix.ewars.entities.structure.controllers

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.StructureBuilder
import net.minestom.server.entity.Player
import java.lang.RuntimeException

class MainBase(
    player: Player
) : Structure<MainBase.Variants>("main-base", Variants.CAMP_FIRE, player) {
    enum class Variants { CAMP_FIRE, CAMP, FORTRESS }
}