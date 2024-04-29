package archyonix.ewars.game.functions

import archyonix.ewars.entities.item.functions.findItemById
import archyonix.ewars.entities.player.addItem
import archyonix.ewars.game
import java.util.*

fun giveStartGameItems() {
    val constructionHummer = findItemById("construction-hammer") ?: return
    for (player in game.getPlayers()) {
        player.addItem(constructionHummer)
    }
    giveCampBlock()
}

private fun giveCampBlock() {
    val random = Random()
    for (team in game.teams!!.values) {
        val index = random.nextInt(team.maxPlayers)
        val player = team.getPlayers()[index]
        val btbTool = findItemById("bmb-tool") ?: return
        player.inventory.addItemStack(btbTool.createItemStack())
    }
}