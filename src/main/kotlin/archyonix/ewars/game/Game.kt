package archyonix.ewars.game

import archyonix.ewars.entities.map.GameMap
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player._getType
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.entities.team.functions.useGameTeams
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.game.events.ChangeGameStageEvent
import archyonix.ewars.shared.lib.events
import archyonix.ewars.shared.lib.gameArgs

class Game {
    val map = GameMap(gameArgs!!.MAP_NAME)
    val instance = GameInstance(map)
    val schedule = instance.scheduler()
    var teams: Map<String, GameTeam>? = null
    var stage = GameStage.EXPECTATION
        set(value) {
            field = value
            events!!.call(ChangeGameStageEvent(value))
        }

    val maxPlayers = map.TEAM_COUNT * map.TEAM_SIZE
    fun getPlayers() = instance.players.filter { it._getType() == PlayerType.PLAYER }
}