package archyonix.ewars.entities.structure

import archyonix.ewars.structureManager

abstract class Structure(
    typeId: String,
    val teamId: String,
) {
    val data: StructureData = structureManager.findDataByTypeId(typeId)
}