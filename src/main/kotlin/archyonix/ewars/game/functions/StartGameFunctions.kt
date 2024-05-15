package archyonix.ewars.game.functions

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.enums.ItemVariant
import archyonix.ewars.entities.player.addItem
import archyonix.ewars.entities.player.updateRecharge
import archyonix.ewars.game

fun giveStartGameItems() {
    for (player in game.getPlayers()) {
        player.addItem(GameItem.AXE.getVariant(ItemVariant.T1)!!)
        player.addItem(GameItem.PICKAXE.getVariant(ItemVariant.T1)!!)
        player.addItem(GameItem.FISHING_ROD.getVariant(ItemVariant.T1)!!)
        player.updateRecharge()
    }
}