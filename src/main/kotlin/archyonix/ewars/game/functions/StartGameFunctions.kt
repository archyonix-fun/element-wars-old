package archyonix.ewars.game.functions

import archyonix.ewars.entities.item.functions.findItemById
import archyonix.ewars.game
import java.util.*

fun giveBuildMainBaseTools() {
    val random = Random()
    for (team in game?.teams!!.values) {
        val index = random.nextInt(team.maxPlayers)
        val player = team.getPlayers()[index]
        val btbTool = findItemById("bmb_tool")
        player.inventory.addItemStack(btbTool.buildItemStack())
    }
}