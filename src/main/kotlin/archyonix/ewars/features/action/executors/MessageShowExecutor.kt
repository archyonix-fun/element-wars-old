package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.shared.libs.withColors
import net.minestom.server.entity.Player

@ClassLibrary.RegisteredClass("MESSAGE_SHOW", ClassLibrary.Type.ACTION_EXECUTOR)
class MessageShowExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val message: String = (properties["message"]
        ?: throw RuntimeException("Message is null")) as String
    private val type: String = (properties["type"] ?: "INFO") as String
    private val messageType = MessageType.valueOf(type.uppercase())
    override fun run(player: Player, caller: Any): Boolean {
        player.showActionBar(message.withColors(), messageType)
        return true
    }
}