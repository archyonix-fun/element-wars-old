package archyonix.ewars.features.action.handler

import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import java.util.*
import kotlin.collections.HashMap

class ActionsHandler: PublicEventNode("action-handler") {
    init {
        addChild(ItemActionsHandler())
    }
    companion object {
        private val limit: MutableMap<Player, Long> = HashMap()

        fun useLimit(player: Player): Boolean {
            val current = Date().time
            if(limit.containsKey(player)) {
                val last = limit[player]!!
                if(current - last < 100) {
                    return false
                }
                limit[player] = current
            } else {
                limit[player] = current
            }
            return true
        }
    }
}