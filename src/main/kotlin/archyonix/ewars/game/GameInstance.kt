package archyonix.ewars.game

import net.minestom.server.instance.InstanceContainer
import net.minestom.server.world.DimensionType
import java.util.*

class GameInstance : InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD) {
}