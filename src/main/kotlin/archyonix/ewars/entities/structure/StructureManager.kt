package archyonix.ewars.entities.structure

import archyonix.ewars.entities.region.Region
import java.io.File
import java.util.*

class StructureManager {
    private val structureTypes: MutableMap<String, StructureData> = HashMap()
    private val structures: MutableMap<UUID, Structure<*>> = HashMap()

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

    fun addStructure(structure: Structure<*>) {
        structures[structure.id] = structure
    }

    fun removeStructure(structure: Structure<*>) {
        structures.remove(structure.id)
    }

    fun validateRegion(region: Region): Boolean {
        for (reg in structures.values.map { it.region }) {
            if (reg.contains(region)) return false
        }
        return true
    }

    fun findStructuresByTeam(teamId: String) = structures.values.filter { it.teamId == teamId }
    fun configByTypeId(typeId: String) = structureTypes[typeId]!!
}