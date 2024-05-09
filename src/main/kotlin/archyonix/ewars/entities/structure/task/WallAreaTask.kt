package archyonix.ewars.entities.structure.task

import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.StructureTower
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game
import archyonix.ewars.structureManager
import java.time.Duration

class WallAreaTask : Runnable {
    override fun run() {
        try {
            game.getPlayers()
                .filter { it.isOnGround }
                .filter { it.itemInMainHand.hasGameItem("construction-hammer") }
                .forEach {
                    val pos = it.getTargetBlockPosition(5)
                    if(pos != null) {
                        val zeroPosition = pos.add(.0, 1.0, .0).withGrid(4)
                        val tower = structureManager.findTower(zeroPosition)
                        if (tower != null && tower.structure.team == it.gameTeam()) {
                            tower.showEffectIfWallIsNone(it, zeroPosition)
                        }
                    }
                }
        } catch (_: Exception) {}

        game.schedule.buildTask(this).delay(Duration.ofMillis(500)).schedule()
    }
}