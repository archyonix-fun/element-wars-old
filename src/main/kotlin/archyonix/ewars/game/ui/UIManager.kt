package archyonix.ewars.game.ui

import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

class UIManager {
    fun createUI(player: Player, name: String): GameUI? {
        val uiConstructor = game.classLibrary.findConstructor<GameUI>(name, ClassLibrary.Type.UI) ?: return null
        return uiConstructor.newInstance(player)
    }
}