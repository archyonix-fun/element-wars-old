package archyonix.ewars.game

import archyonix.ewars.entities.map.GameMap
import archyonix.ewars.shared.lib.gameArgs

class Game {
    val map = GameMap(gameArgs!!.MAP_NAME)
    val instance = GameInstance(map)
}