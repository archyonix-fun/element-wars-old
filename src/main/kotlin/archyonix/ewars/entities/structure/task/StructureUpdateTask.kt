package archyonix.ewars.entities.structure.task

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.functions.showRegion
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game
import archyonix.ewars.game.enums.GameStage
import net.minestom.server.tag.Tag
import java.time.Duration

class StructureUpdateTask : Runnable {
    override fun run() {
        if(game.stage == GameStage.RUNTIME) {
            showStructureSchemaArea()
            game.structureManager.updateStructures()
        }
        game.schedule.buildTask(this).delay(Duration.ofMillis(250)).schedule()
    }

    private fun showStructureSchemaArea() {
        game.getPlayers()
            .filter { it.isOnGround }
            .filter { it.itemInMainHand.hasGameItem(GameItem.STRUCTURE_SCHEMA) }
            .forEach {
                val structureId = it.itemInMainHand.getTag(Tag.String("structureId"))
                val structureConfig = game.structureManager.getConfig(structureId)

                val zeroPosition = it.position.withGrid(structureConfig.grid)
                val region2D = Region2D.fromSizeArea(zeroPosition, structureConfig.area.size)
                showRegion(region2D, it)
            }
    }
}