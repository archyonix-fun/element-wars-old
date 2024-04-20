package archyonix.ewars.game

import archyonix.ewars.entities.map.GameMap
import archyonix.ewars.shared.lib.gameArgs
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.getType

class Game {
    val map = GameMap(gameArgs!!.MAP_NAME)
    val instance = GameInstance(map)

    val maxPlayers = map.TEAM_COUNT * map.TEAM_SIZE
    fun getPlayers() = instance.players.filter { it.getType() == PlayerType.PLAYER }
}