package archyonix.ewars.entities.item

import archyonix.ewars.structureManager
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

class StructureBuildItem(itemStack: ItemStack) {
    private val structureId = itemStack.getTag(StructureIdTag) ?: throw RuntimeException("structureId is not exists")

    val config = structureManager.getConfig(structureId)
    val variant = config.variants[0]
}

private const val groupName = "STRUCTURE_BUILD"

private val GroupTag = Tag.String("group")
private val StructureIdTag = Tag.String("#structureId")

fun ItemStack.hasStructureBuildItem(): Boolean {
    if (hasTag(GroupTag)) return false
    return getTag(GroupTag) == groupName
}

fun ItemStack.addStructureBuildTags(structureId: String) = this
    .withTag(GroupTag, groupName)
    .withTag(StructureIdTag, structureId)

fun ItemStack.asStructureBuildItem() =
    if (hasStructureBuildItem()) StructureBuildItem(this) else null