package archyonix.ewars.game

import archyonix.ewars.entities.map.GameMap
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.getType
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.game.events.ChangeGameStageEvent
import archyonix.ewars.shared.lib.events
import archyonix.ewars.shared.lib.gameArgs

class Game {
    val map = GameMap(gameArgs!!.MAP_NAME)
    val instance = GameInstance(map)
    val schedule = instance.scheduler()
    var stage = GameStage.EXPECTATION
        set(value) {
            field = value
            events!!.call(ChangeGameStageEvent(value))
        }

    val maxPlayers = map.TEAM_COUNT * map.TEAM_SIZE
    fun getPlayers() = instance.players.filter { it.getType() == PlayerType.PLAYER }
}