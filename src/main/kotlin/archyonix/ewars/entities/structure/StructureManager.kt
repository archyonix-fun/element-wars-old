package archyonix.ewars.entities.structure

class StructureManager {
    private val structureTypes: MutableMap<String, StructureData> = HashMap()
    private val structures: MutableSet<Structure> = HashSet()

    fun addType(data: StructureData) {
        structureTypes[data.id] = data
    }

    fun findStructuresByTeam(teamId: String) = structures.filter { it.teamId == teamId }
}