package archyonix.ewars.entities.structure

import archyonix.ewars.shared.libs.AppSchematic
import java.io.File

class StructureData(
    val id: String
) {
    val schematic = AppSchematic()

    init {
        val inputStream = File("config/structures/$id/schemas/1.schem").inputStream()
        schematic.read(inputStream)
    }
}