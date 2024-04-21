package archyonix.ewars.entities.map

import archyonix.ewars.shared.WorldLoader
import archyonix.ewars.shared.libs.toPos
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.IChunkLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.Generator
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class GameMap(mapName: String) {
    private val config = YamlConfiguration.loadConfiguration(File("maps/$mapName/map.yaml"))
    val ID: String = config.getString("name") ?: mapName
    val DISPLAY_NAME: String = config.getString("displayName")!!
    val TEAM_COUNT: Int = config.getInt("teamCount")
    val TEAM_SIZE: Int = config.getInt("teamSize")
    val WORLD_SPAWN: Pos = config.getString("worldSpawn")!!.toPos()
    val SPAWN_LIST: List<Pos> = config.getStringList("spawnList").map { it.toPos() }

    val WORLD_GENERATOR: Generator = Generator { unit -> unit.modifier().fill(Block.AIR) }
    val WORLD_LOADER: IChunkLoader = WorldLoader("maps/$mapName/world")
}