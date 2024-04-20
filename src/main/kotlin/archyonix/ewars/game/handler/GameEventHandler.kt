package archyonix.ewars.game.handler

import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.handler.SpectatorEventHandler
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.game.events.ChangeGameStageEvent
import archyonix.ewars.game.events.InitGameEvent
import archyonix.ewars.game.events.PlayerJoinTheGameEvent
import archyonix.ewars.game.functions.configurePlayer
import archyonix.ewars.game.functions.configureSpectator
import archyonix.ewars.game.task.ExpectationTask
import archyonix.ewars.shared.lib.events
import archyonix.ewars.shared.lib.game
import net.minestom.server.event.PublicEventNode

class GameEventHandler : PublicEventNode("game") {
    init {
        addListener(PlayerJoinTheGameEvent::class.java) { event ->
            when (event.playerType) {
                PlayerType.PLAYER -> configurePlayer(event.player)
                PlayerType.SPECTATOR -> configureSpectator(event.player)
            }

            val username = event.player.username
            val type = event.playerType.name
            val count = game!!.getPlayers().size
            val max = game!!.maxPlayers

            println("$username joined to server. PlayerType=$type ($count/$max)")
        }
        addListener(InitGameEvent::class.java) { event ->
            events!!.call(ChangeGameStageEvent(event.game.stage))
        }
        addListener(ChangeGameStageEvent::class.java) { event ->
            when (event.stage) {
                GameStage.EXPECTATION -> {
                    game!!.schedule.scheduleNextTick(ExpectationTask())
                }
                GameStage.COUNTDOWN -> {}

                else -> {}
            }
        }

        addChild(SpectatorEventHandler())
    }
}