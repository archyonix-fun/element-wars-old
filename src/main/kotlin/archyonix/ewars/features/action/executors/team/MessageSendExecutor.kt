package archyonix.ewars.features.action.executors.team

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.shared.libs.withColors
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("TEAM_MESSAGE_SEND", ClassLibrary.Type.ACTION_EXECUTOR)
class MessageSendExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val message: String = (properties["message"]
        ?: throw RuntimeException("Message is null")) as String

    override fun run(player: Player, caller: Any): Boolean {
        player.gameTeam().showMessage(message.withColors())
        return true
    }
}