package archyonix.ewars.entities.structure

import archyonix.ewars.shared.libs.CellPosition
import archyonix.ewars.structureManager
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.floor

abstract class Structure(
    typeId: String,
    val teamId: String,
    val pos: CellPosition
) {
    val id: UUID = UUID.randomUUID()
    val config: StructureData = structureManager.configByTypeId(typeId)
    val data: MutableMap<String, String> = HashMap()
    val region: Region = createRegion()
    val tags: MutableSet<StructureTag> = HashSet()

    private fun createRegion(): Region {
        val radius = floor(config.area / 2.0).toInt()
        return Region(pos.x - radius, pos.z - radius, pos.x + radius, pos.z + radius)
    }
}

enum class StructureTag {
    BREAKABLE,
}