package archyonix.ewars.features.action.executors.ui

import archyonix.ewars.entities.ui.UI
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("UI_CLOSE", ClassLibrary.Type.ACTION_EXECUTOR)
class CloseExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        if(caller !is UI) throw RuntimeException("Executor can only be called by a ui")
        caller.close()
        return true
    }
}