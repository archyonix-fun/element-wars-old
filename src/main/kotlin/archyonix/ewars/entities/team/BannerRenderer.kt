package archyonix.ewars.entities.team

import com.syntaxphoenix.syntaxapi.nbt.tools.NbtParser
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
    private val compound = NbtParser.fromMap(
        mapOf(
            Pair(
                "Patterns", listOf(
                    mapOf(
                        Pair("Color", firstColor.ordinal),
                        Pair("Pattern", "gra")
                    ),
                    mapOf(
                        Pair("Color", secondColor.ordinal),
                        Pair("Pattern", "gru")
                    )
                )
            )
        )
    )

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
            }).withNbt(SNBTParser(StringReader(compound.toMSONString())).parse() as NBTCompound)
        else return block
    }
}