package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.features.shop.CostInfo
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@ClassLibrary.RegisteredClass("PAY", ClassLibrary.Type.ACTION_EXECUTOR)
class PayExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val cost = CostInfo.import(properties["cost"] as ConfigurationSection)
        ?: throw RuntimeException("Cost is null")

    override fun run(player: Player, caller: Any): Boolean {
        if (!cost.pay(player)) {
            player.showActionBar("У команды недостаточно средств", MessageType.WARNING)
            return false
        }
        return true
    }
}