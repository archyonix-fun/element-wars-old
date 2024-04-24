package archyonix.ewars.entities.structure

import archyonix.ewars.game
import archyonix.ewars.shared.libs.AppSchematic
import net.minestom.server.coordinate.Pos
import net.minestom.server.utils.Direction
import java.io.File

class StructureData(
    val id: String
) {
    val schematic = AppSchematic(Direction.NORTH)

    init {
        val inputStream = File("config/structures/$id/schemas/1.schem").inputStream()
        schematic.read(inputStream)
    }

    fun build(pos: Pos) {
        schematic.build(game!!.instance, pos)
    }
}