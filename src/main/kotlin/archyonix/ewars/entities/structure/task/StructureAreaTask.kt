package archyonix.ewars.entities.structure.task

import archyonix.ewars.entities.item.asStructureBuildItem
import archyonix.ewars.entities.item.hasStructureBuildItem
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.functions.showRegion
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game
import net.minestom.server.coordinate.Vec
import java.time.Duration
import kotlin.math.floor

class StructureAreaTask : Runnable {
    override fun run() {
        game.getPlayers()
            .filter { it.isOnGround }
            .filter { it.itemInMainHand.hasStructureBuildItem() }
            .forEach {
                val buildItem = it.itemInMainHand.asStructureBuildItem()!!
                val structureConfig = buildItem.config
                var zeroPosition = it.position.asVec()
                if(structureConfig.name == "TOWER") zeroPosition = zeroPosition.withGrid() as Vec
                val region = Region2D(zeroPosition, floor(structureConfig.area / 2.0).toInt(), structureConfig.area % 2 == 0)
                showRegion(region, it)
            }

        game.schedule.buildTask(this).delay(Duration.ofMillis(200)).schedule()
    }
}