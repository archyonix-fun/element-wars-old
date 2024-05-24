package archyonix.ewars.features.action.executors.structure

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.enums.ItemVariant
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.shared.libs.withColors
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

@ClassLibrary.RegisteredClass("STRUCTURE_SCHEMA_GIVE", ClassLibrary.Type.ACTION_EXECUTOR)
class SchemaGiveExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val structureId: String = (properties["structureId"]
        ?: throw RuntimeException("Structure id is null")) as String
    private val amount: Int = (properties["amount"] ?: 1) as Int

    override fun run(player: Player, caller: Any): Boolean {
        return player.inventory.addItemStack(buildSchemaItem(structureId))
    }

    private fun buildSchemaItem(structureId: String): ItemStack {
        val config = game.structureManager.getConfig(structureId)
        val variant = config.variants[0]

        return GameItem.STRUCTURE_SCHEMA
            .getVariant(ItemVariant.DEFAULT)!!
            .createItemStack(amount)
            .withDisplayName(variant.name.withColors())
            .withTag(Tag.String("structureId"), structureId)
            .withTag(Tag.String("icon"), variant.id)
    }
}