package archyonix.ewars.entities.structure

import java.io.File

class StructureManager {
    private val structureTypes: MutableMap<String, StructureData> = HashMap()
    private val structures: MutableSet<Structure> = HashSet()

    init {
        val structureDir = File("config/structures")
        if (structureDir.exists() && structureDir.isDirectory) {
            for (file in structureDir.list()!!) {
                addType(StructureData(file))
            }
        }
    }

    private fun addType(data: StructureData) {
        structureTypes[data.id] = data
    }

    fun findStructuresByTeam(teamId: String) = structures.filter { it.teamId == teamId }
    fun findDataByTypeId(typeId: String) = structureTypes[typeId]!!
}