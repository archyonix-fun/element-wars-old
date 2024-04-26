package archyonix.ewars.entities.structure

import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.region.Region
import archyonix.ewars.game
import archyonix.ewars.shared.libs.firstFromTop
import archyonix.ewars.shared.libs.getDirection
import archyonix.ewars.structureManager
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Direction
import java.util.*
import kotlin.math.floor

abstract class Structure<Variant : Enum<*>>(
    typeId: String,
    builder: Player,
) {
    private val config: StructureData = structureManager.configByTypeId(typeId)
    private val direction: Direction = builder.getDirection()
    private val pos: Point = builder.position.sub(config.area / 2.0).firstFromTop()
    private var currentVariant: Variant? = null

    val id: UUID = UUID.randomUUID()
    val teamId: String = builder._getTeam().id
    val data: MutableMap<String, String> = HashMap()

    val region: Region = createRegion()

    init {
        if (!structureManager.validateRegion(region)) throw RuntimeException("Ошибка")
    }

    private fun createRegion(): Region {
        val radius = floor(config.area / 2.0).toInt()
        return Region(pos, radius)
    }

    fun build(variant: Variant) {
        deconstruct()
        currentVariant = variant
        config.variants[currentVariant!!.name]!!.getSchema(direction).build(game.instance, pos)
    }

    private fun deconstruct() {
        if (currentVariant != null) {
            config.variants[currentVariant!!.name]!!.getSchema(direction)
                .build(game.instance, pos, fill = Block.AIR)
        }
    }

    fun destroy() {
        deconstruct()
        structureManager.removeStructure(this)
    }
}