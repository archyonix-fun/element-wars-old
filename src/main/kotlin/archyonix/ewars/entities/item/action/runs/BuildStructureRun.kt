package archyonix.ewars.entities.item.action.runs

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.ActionRun
import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.MainBase
import archyonix.ewars.regionManager
import archyonix.ewars.shared.libs.CellPosition
import archyonix.ewars.shared.libs.getDirection
import archyonix.ewars.structureManager
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player

class BuildStructureRun() : ActionRun("BUILD_STRUCTURE") {
    override fun run(player: Player?, item: GameItem?, data: Map<String, String>) {
        val structureId = data["structureId"] ?: throw RuntimeException("Unknown action data exception")
        val structure = MainBase(player!!)
        if(!regionManager.validateRegion(structure.region)) player.sendMessage("Места нет")
    }
}