package archyonix.ewars.features.action.executors

import archyonix.ewars.features.action.Action
import archyonix.ewars.game
import net.minestom.server.entity.Player

@Action.RegisteredExecutor("OPEN_UI")
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