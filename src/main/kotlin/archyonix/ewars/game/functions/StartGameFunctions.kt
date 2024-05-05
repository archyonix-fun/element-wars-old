package archyonix.ewars.game.functions

import archyonix.ewars.entities.item.functions.findItemById
import archyonix.ewars.entities.player.addItem
import archyonix.ewars.entities.player.updateAttackSpeed
import archyonix.ewars.game
import net.minestom.server.entity.Player

fun giveStartGameItems() {
    val constructionHummer = findItemById("construction-hammer") ?: return
    for (player in game.getPlayers()) {
        player.addItem(constructionHummer)
        // TODO remove next line in final version
        givePickaxes(player)

        // Update attack speed
        player.updateAttackSpeed(constructionHummer)
    }
}

private fun giveAxes(player: Player) {
    player.addItem(findItemById("WOODEN_AXE") ?: return)
    player.addItem(findItemById("STONE_AXE") ?: return)
    player.addItem(findItemById("IRON_AXE") ?: return)
    player.addItem(findItemById("ELEMENTARY_AXE") ?: return)
}

private fun givePickaxes(player: Player) {
    player.addItem(findItemById("WOODEN_PICKAXE") ?: return)
    player.addItem(findItemById("STONE_PICKAXE") ?: return)
    player.addItem(findItemById("IRON_PICKAXE") ?: return)
    player.addItem(findItemById("ELEMENTARY_PICKAXE") ?: return)
}