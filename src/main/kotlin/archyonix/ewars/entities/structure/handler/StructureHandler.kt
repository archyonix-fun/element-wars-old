package archyonix.ewars.entities.structure.handler

import archyonix.ewars.entities.structure.task.ShowStructureAreaTask
import archyonix.ewars.game
import net.minestom.server.event.PublicEventNode

class StructureHandler: PublicEventNode("structure-handler") {
    init {
        game.schedule.scheduleNextTick(ShowStructureAreaTask())
    }
}