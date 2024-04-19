package archyonix.ewars.game

import archyonix.ewars.entities.map.GameMap
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.world.DimensionType
import java.util.*

class GameInstance(map: GameMap) : InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD) {
    init {
        setGenerator(map.WORLD_GENERATOR)
        chunkLoader = map.WORLD_LOADER

        timeRate = 0

        MinecraftServer.getInstanceManager().registerInstance(this)
    }
}