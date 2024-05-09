package archyonix.ewars.entities.structure

import net.minestom.server.coordinate.Point
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class StructureManager {
    val structureSet: MutableSet<Structure> = HashSet()
    val towerSet: MutableSet<StructureTower> = HashSet()
    private val configMap: MutableMap<String, StructureConfig> = HashMap()

    init {
        val structureDir = File("config/structures")
        if (structureDir.exists() && structureDir.isDirectory) {
            for (dir in structureDir.listFiles()!!) {
                val config = YamlConfiguration.loadConfiguration(File(dir, "structure.yaml"))
                val structureConfig = StructureConfig(config)
                configMap[structureConfig.name] = structureConfig
            }
        }
    }

    fun validateRegion(structure: Structure): Boolean {
        for (it in structureSet) {
            if (it.region.contains(structure.region)) return false
        }
        return true
    }

    fun findStructure(pos: Point): Structure? {
        return structureSet.find { it.region.contains(pos) }
            ?: towerSet.map { it.structure }.find { it.region.contains(pos) }
    }

    fun findTower(pos: Point) = towerSet
        .find { it.structure.region.contains(pos) }

    fun getConfig(structureName: String) =
        configMap[structureName] ?: throw RuntimeException("Unknown structure config: $structureName")

    fun addStructure(structure: Structure) {
        if (structure.isTower()) towerSet.add(StructureTower(structure))
        else structureSet.add(structure)
    }
}