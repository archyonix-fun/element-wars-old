package archyonix.ewars.game.handler

import archyonix.ewars.entities.item.action.handler.ItemActionHandler
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.handler.SpectatorEventHandler
import archyonix.ewars.entities.structure.StructureData
import archyonix.ewars.entities.team.functions.distributeTeams
import archyonix.ewars.entities.team.functions.useGameTeams
import archyonix.ewars.events
import archyonix.ewars.game
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.game.events.ChangeGameStageEvent
import archyonix.ewars.game.events.InitGameEvent
import archyonix.ewars.game.events.PlayerJoinTheGameEvent
import archyonix.ewars.game.functions.configurePlayer
import archyonix.ewars.game.functions.configureSpectator
import archyonix.ewars.game.functions.giveBuildMainBaseTools
import archyonix.ewars.game.task.CountdownTask
import archyonix.ewars.game.task.ExpectationTask
import net.minestom.server.coordinate.Pos
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

            val data = StructureData("main-base")
            data.build(Pos(5.0, 0.0, 5.0))
        }
        addListener(InitGameEvent::class.java) { event ->
            events!!.call(ChangeGameStageEvent(event.game.stage))
        }
        addListener(ChangeGameStageEvent::class.java) { event ->
            when (event.stage) {
                GameStage.EXPECTATION -> {
                    game!!.schedule.scheduleNextTick(ExpectationTask())
                }

                GameStage.COUNTDOWN -> {
                    game!!.schedule.scheduleNextTick(CountdownTask())
                }

                GameStage.RUNTIME -> {
                    game!!.teams = useGameTeams()
                    distributeTeams()
                    giveBuildMainBaseTools()
                }

                else -> {}
            }
        }

        addChild(ItemActionHandler())
        addChild(SpectatorEventHandler())
    }
}