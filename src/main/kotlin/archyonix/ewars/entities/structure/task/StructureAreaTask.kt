package archyonix.ewars.entities.structure.task

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.functions.showRegion
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game
import net.minestom.server.tag.Tag
import java.time.Duration

class StructureAreaTask : Runnable {
    override fun run() {
        game.getPlayers()
            .filter { it.isOnGround }
            .filter { it.itemInMainHand.hasGameItem(GameItem.STRUCTURE_SCHEMA) }
            .forEach {
                val structureId = it.itemInMainHand.getTag(Tag.String("structureId"))
                val structureConfig = game.structureManager.getConfig(structureId)

                val zeroPosition = it.position.withGrid(structureConfig.grid)
                val region2D = Region2D(
                    zeroPosition,
                    structureConfig.area.radius,
                    structureConfig.area.size % 2 == 0
                )
                showRegion(region2D, it)
            }

        game.schedule.buildTask(this).delay(Duration.ofMillis(200)).schedule()
    }
}