package archyonix.ewars.entities.structure.controllers

import archyonix.ewars.entities.structure.Structure
import net.minestom.server.entity.Player

class MainBase(
    builder: Player
) : Structure<MainBase.Variants>("main-base", builder) {

    init { build(Variants.CAMP_FILE) }

    enum class Variants { CAMP_FILE, CAMP, FORTRESS }
}