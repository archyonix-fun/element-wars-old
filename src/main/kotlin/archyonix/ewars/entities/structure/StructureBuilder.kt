package archyonix.ewars.entities.structure

import archyonix.ewars.game
import archyonix.ewars.entities.region.Region
import net.minestom.server.instance.block.Block
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class StructureBuilder<Variant: Enum<*>>(
    structure: Structure<Variant>,
    variant: Variant
) {
    private val config: StructureData = structure.config
    private val pos = structure.pos
    private val maxAnother = config.area * 2
    private val baseRegion: Region
    private val airRegion: Region
    private var buildProgress = 0
    private val radius = floor(config.area / 2.0).toInt()
    private val variant = config.variants[variant.name]
    private val direction = structure.direction
    private val schema = this.variant!!.getSchema(direction)

    init {
        val minX = pos.blockX() - radius
        val minZ = pos.blockZ() - radius
        val maxX = pos.blockX() + radius
        val maxZ = pos.blockZ() + radius

        val y = pos.blockY()

        this.baseRegion = Region(minX, y, minZ, maxX, y, maxZ)
        this.airRegion = Region(minX, y + 1, minZ, maxX, y + config.area, maxZ)
    }

    fun validateBase(): Boolean {
        val notSolidBlockCount = this.baseRegion.notSolidBlockCount(maxAnother)
        return notSolidBlockCount < maxAnother
    }

    fun validateAir(): Boolean {
        val solidBlockCount = this.airRegion.solidBlockCount(maxAnother)
        return solidBlockCount < maxAnother
    }

    fun fillBase() = baseRegion.fill(Block.GRASS_BLOCK, Block.COARSE_DIRT, Block.ROOTED_DIRT, Block.DIRT)
    fun clearAir() = airRegion.fill(Block.AIR)

    fun placeBlueprint(): Boolean {
        val position = pos.add((-radius).toDouble(), 1.0, (-radius).toDouble())
        schema.build(game.instance, position, fill = Block.SCAFFOLDING)

        return true
    }

    fun addProgress(power: Int): Boolean {
        buildProgress = max(0, min(variant!!.buildPoints, buildProgress + power))
        build()
        return buildProgress != variant.buildPoints
    }

    private fun build(): Boolean {
        val position = pos.add((-radius).toDouble(), 1.0, (-radius).toDouble())
        schema.build(game.instance, position,
            percent = buildProgress.toFloat() / variant!!.buildPoints,
            particles = true
        )

        return true
    }
}