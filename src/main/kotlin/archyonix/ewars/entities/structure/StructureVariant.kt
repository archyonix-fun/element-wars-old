package archyonix.ewars.entities.structure

import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.shared.libs.AppSchematic
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.io.File

class StructureVariant(
    val structureId: String,
    private val section: ConfigurationSection,
) {
    val variantName: String = section.name
    val displayName: String = section.getString("displayName")!!
    val buildPoints: Int = section.getInt("buildPoints")
    val data: Map<String, String> = readData()
    private val schema: String = section.getString("schema")!!
    fun getSchema(direction: Direction, team: GameTeam): AppSchematic {
        val schematic = AppSchematic(direction, team)
        val inputStream = File("config/structures/$structureId/$schema").inputStream()
        schematic.read(inputStream)
        inputStream.close()

        return schematic
    }

    private fun readData(): Map<String, String> {
        val dataSection = section.getConfigurationSection("data") ?: return HashMap()
        val data: MutableMap<String, String> = HashMap()
        for (key in dataSection.getKeys(false)) {
            data[key] = dataSection.getString(key)!!
        }
        return data
    }
}