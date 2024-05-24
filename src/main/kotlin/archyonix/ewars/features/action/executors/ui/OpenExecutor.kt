package archyonix.ewars.features.action.executors.ui

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.ui.UI
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("UI_OPEN", ClassLibrary.Type.ACTION_EXECUTOR)
class OpenExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val path: String = (properties["path"]
        ?: throw RuntimeException("UI path is null")) as String
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem.Variant) throw RuntimeException("Executor can only be called by a game item")
        UI.Constructor(path).buildUI(player, caller)
        return true
    }
}