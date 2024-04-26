package archyonix.ewars.entities.item.action.runs

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.ActionRun
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.StructureBuilder
import archyonix.ewars.entities.structure.controllers.MainBase
import archyonix.ewars.structureManager
import net.minestom.server.entity.Player
import java.lang.Exception

class BuildStructureRun() : ActionRun("BUILD_STRUCTURE") {
    override fun run(player: Player?, item: GameItem?, data: Map<String, String>) {
        val structureId = data["structureId"] ?: throw RuntimeException("Unknown action data exception")
        try {
            val structure = StructureBuilder(player!!).build<Structure<*>>(structureId)
            structureManager.addStructure(structure)
        } catch (e: Exception) {
            player?.sendMessage(e.message ?: "Места нет!")
        }
    }
}