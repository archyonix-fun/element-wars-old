package archyonix.ewars.features.action.executors

import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("OPEN_UI", ClassLibrary.Type.EXECUTOR)
class UIExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player): Boolean {
        val name = (properties["name"] ?: return false) as String
        val ui = game.uiManager.createUI(player, name) ?: return false
        ui.show()

        return true
    }
}