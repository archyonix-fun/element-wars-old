package archyonix.ewars.entities.team

import net.minestom.server.color.DyeColor
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import net.minestom.server.utils.NamespaceID
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.parser.SNBTParser
import java.io.StringReader


class BannerRenderer(
    team: GameTeam,
) {
    private val firstColor = DyeColor.valueOf(team.bannerColorFirst)
    private val secondColor = DyeColor.valueOf(team.bannerColorSecond)
    private val compound = SNBTParser(
        StringReader(
            "{Patterns:[{Color:${firstColor.ordinal},Pattern:\"gra\"},{Color:${secondColor.ordinal},Pattern:\"gru\"}]}"
        )
    ).parse() as NBTCompound

    fun render(block: Block): Block {
        if (
            block.name().contains("banner")
        ) return Block.fromNamespaceId(block.name().replace("white", firstColor.name.lowercase()))!!
            .withProperties(block.properties()).withHandler(object : BlockHandler {
                override fun getNamespaceId(): NamespaceID {
                    return block.namespace()
                }

                override fun getBlockEntityTags(): MutableCollection<Tag<*>> {
                    return mutableListOf(
                        Tag.NBT("Patterns")
                    )
                }
            }).withNbt(compound)
        else return block
    }
}