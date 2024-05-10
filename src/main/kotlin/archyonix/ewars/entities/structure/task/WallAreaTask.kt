package archyonix.ewars.entities.structure.task

import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.StructureTower
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game
import archyonix.ewars.structureManager
import java.time.Duration

class WallAreaTask : Runnable {
    override fun run() {
        try {
            for (player in game.getPlayers()) {
                if (!player.isOnGround) continue
                if (!player.itemInMainHand.hasGameItem("CONSTRUCTION_HAMMER")) continue
                val targetPoint = player.getTargetBlockPosition(5) ?: continue
                val zeroPosition = targetPoint.add(.0, 1.0, .0).withGrid(4)
                val structure = structureManager.findStructure<Structure>(zeroPosition) ?: continue
                val tower = if (structure is StructureTower) structure else continue
                if (player.gameTeam() != tower.team) continue
                tower.showWallSketch(player, zeroPosition)
            }
        } catch (_: Exception) {
        }
        game.schedule.buildTask(this).delay(Duration.ofMillis(500)).schedule()
    }
}