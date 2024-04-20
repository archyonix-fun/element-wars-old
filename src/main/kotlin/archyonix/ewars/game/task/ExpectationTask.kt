package archyonix.ewars.game.task

import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.shared.lib.game
import archyonix.ewars.shared.lib.shared.libs.createTitle
import net.kyori.adventure.title.Title
import java.time.Duration
import kotlin.system.exitProcess

class ExpectationTask : Runnable {
    private var endTime: Int = 60
    override fun run() {
        val count = game!!.getPlayers().size
        val max = game!!.maxPlayers

        if (count <= 0) {
            if (endTime-- <= 0) exitProcess(0)
        } else endTime = 60

        val title: Title = createTitle(
            "Ожидание игроков",
            "$count/$max",
            1200L
        )
        for (player in game!!.getPlayers()) {
            player.showTitle(title)
        }

        val isExpectation = game!!.stage == GameStage.EXPECTATION
        if (count == max && isExpectation) {
            game!!.stage = GameStage.COUNTDOWN
        } else if (isExpectation) {
            game!!.schedule.buildTask(this).delay(Duration.ofSeconds(1)).schedule()
        }
    }
}