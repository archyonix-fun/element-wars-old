package archyonix.ewars.features.action.handler

import net.minestom.server.event.PublicEventNode

class ActionsHandler: PublicEventNode("action-handler") {
    init {
        addChild(ItemActionsHandler())
    }
}