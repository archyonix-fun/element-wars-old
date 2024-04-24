package archyonix.ewars.game.task

import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.game
import archyonix.ewars.shared.libs.createTitle
import java.time.Duration

class CountdownTask : Runnable {
    private var countdown: Int = 10
    override fun run() {
        val count = game.getPlayers().size
        val max = game.maxPlayers

        val title = createTitle(
            "$countdown",
            "Обратный отсчет",
            1200
        )

        val isCountdown = game.stage == GameStage.COUNTDOWN
        if (count == max && isCountdown) {
            if (countdown-- <= 0) game.stage = GameStage.RUNTIME
            else {
                for (player in game.getPlayers()) {
                    player.showTitle(title)
                }
                game.schedule.buildTask(this).delay(Duration.ofSeconds(1)).schedule()
            }
        } else if(isCountdown) game.stage = GameStage.EXPECTATION
    }
}