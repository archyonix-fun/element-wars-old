package archyonix.ewars.entities.structure

import archyonix.ewars.structureManager
import net.minestom.server.entity.Player

@Suppress("UNCHECKED_CAST")
class StructureBuilder(
    private val player: Player
) {
    fun <T : Structure<*>> build(structureId: String): Structure<*> {
        val config = structureManager.configByTypeId(structureId)
        val structureClass = javaClass.classLoader.loadClass(config.controllerURI)
        val structure = structureClass.getDeclaredConstructor(Player::class.java).newInstance(player) as Structure<*>
        return structure as T
    }
}