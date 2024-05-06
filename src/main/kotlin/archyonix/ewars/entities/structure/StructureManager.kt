package archyonix.ewars.entities.structure

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class StructureManager {
    val structureSet: MutableSet<Structure> = HashSet()
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
            if(it.region.contains(structure.region)) return false
        }
        return true
    }

    fun findStructure(pos: Point) =
        structureSet.find { it.region.contains(pos) }

    fun getConfig(structureName: String) =
        configMap[structureName] ?: throw RuntimeException("Unknown structure config: $structureName")

    fun addStructure(structure: Structure) = structureSet.add(structure)
}