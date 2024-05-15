package archyonix.ewars.game.handler

import archyonix.ewars.entities.item.action.handler.ItemActionHandler
import archyonix.ewars.entities.item.action.handler.ItemHandler
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.handler.SpectatorEventHandler
import archyonix.ewars.entities.resource.handler.ResourceHandler
import archyonix.ewars.entities.resource.handler.ResourceWaterHandler
import archyonix.ewars.entities.structure.handler.StructureBuildHandler
import archyonix.ewars.entities.structure.handler.StructureHandler
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
import archyonix.ewars.game.functions.giveStartGameItems
import archyonix.ewars.game.task.CountdownTask
import archyonix.ewars.game.task.ExpectationTask
import archyonix.ewars.game.task.HiddenTask
import net.minestom.server.entity.GameMode
import net.minestom.server.event.PublicEventNode
import java.util.*

class GameEventHandler : PublicEventNode("game") {
    init {
        addListener(PlayerJoinTheGameEvent::class.java) { event ->
            when (event.playerType) {
                PlayerType.PLAYER -> configurePlayer(event.player)
                PlayerType.SPECTATOR -> configureSpectator(event.player)
            }
            if (game.isDebug) event.player.setGameMode(GameMode.CREATIVE)

            val username = event.player.username
            val type = event.playerType.name
            val count = game.getPlayers().size
            val max = game.maxPlayers

            println("$username joined to server. PlayerType=$type ($count/$max)")
        }
        addListener(InitGameEvent::class.java) { event ->
            events.call(ChangeGameStageEvent(event.game.stage))
        }

        addListener(ChangeGameStageEvent::class.java) { event ->
            when (event.stage) {
                GameStage.EXPECTATION -> {
                    game.schedule.scheduleNextTick(ExpectationTask())
                }

                GameStage.COUNTDOWN -> {
                    game.schedule.scheduleNextTick(CountdownTask())
                }

                GameStage.RUNTIME -> {
                    game.schedule.scheduleNextTick(HiddenTask())
                    game.startTime = Date().time + (3600000 * 3)

                    game.teams = useGameTeams()
                    distributeTeams()
                    giveStartGameItems()
                }

                else -> {}
            }
        }

        // Features
        addChild(ResourceHandler())
        addChild(ResourceWaterHandler())

        addChild(StructureBuildHandler())
        addChild(StructureHandler())
        addChild(ItemActionHandler())
        addChild(ItemHandler())
        addChild(SpectatorEventHandler())
    }
}