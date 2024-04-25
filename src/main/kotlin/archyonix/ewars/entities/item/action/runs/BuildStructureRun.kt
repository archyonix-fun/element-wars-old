package archyonix.ewars.entities.item.action.runs

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.ActionRun
import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.regionManager
import archyonix.ewars.shared.libs.CellPosition
import archyonix.ewars.shared.libs.getDirection
import archyonix.ewars.structureManager
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player

class BuildStructureRun() : ActionRun("BUILD_STRUCTURE") {
    override fun run(player: Player?, item: GameItem?, data: Map<String, String>) {
        val structureId = data["structureId"] ?: throw RuntimeException("Unknown action data exception")
        val pos = player!!.getTargetBlockPosition(5) ?: return

        val structure = Structure(structureId, "red", CellPosition(pos), player.getDirection())
        if(!regionManager.validateRegion(structure.region)) player.sendMessage("Хуйня ебаная места нет сука бля")
        else {
            structureManager.addStructure(structure)
            structure.build()
        }
    }
}