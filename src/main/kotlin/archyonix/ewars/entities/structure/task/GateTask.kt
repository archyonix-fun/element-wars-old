package archyonix.ewars.entities.structure.task

import archyonix.ewars.entities.structure.updateAllGates
import archyonix.ewars.game
import java.time.Duration

class GateTask : Runnable {
    override fun run() {
        updateAllGates()
        game.schedule.buildTask(this).delay(Duration.ofMillis(500)).schedule()
    }
}