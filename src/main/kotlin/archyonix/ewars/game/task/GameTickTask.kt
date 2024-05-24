package archyonix.ewars.game.task

import archyonix.ewars.game
import archyonix.ewars.game.events.GameTickEvent
import java.time.Duration

class GameTickTask : Runnable {
    override fun run() {
        game.handler.call(GameTickEvent())
        game.schedule.buildTask(this).delay(Duration.ofMillis(250)).schedule()
    }
}