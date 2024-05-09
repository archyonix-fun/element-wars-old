package archyonix.ewars.entities.structure

import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.region.Zone
import net.minestom.server.instance.block.Block

class ZoneValidator(
    region: Region2D,
    startY: Int,
    endY: Int,
) {
    private val maxAnother = (region.endX - region.startX) * 2
    private val airZone = Zone(region.startX, startY, region.startZ, region.endX, endY, region.endZ)
    private val baseZone = Zone(region.startX, startY - 1, region.startZ, region.endX, startY - 1, region.endZ)

    private fun validateBase(): Boolean {
        val notSolidBlockCount = this.baseZone.notSolidBlockCount(maxAnother)
        return notSolidBlockCount < maxAnother
    }

    private fun validateAir(): Boolean {
        val solidBlockCount = this.airZone.solidBlockCount(maxAnother)
        return solidBlockCount < maxAnother
    }

    private fun fillBase() = baseZone.fill(Block.GRASS_BLOCK, Block.GRASS_BLOCK, Block.COARSE_DIRT, Block.ROOTED_DIRT, Block.DIRT)
    fun clear() = airZone.fill(Block.AIR)

    fun validateAndPrepare(): Boolean {
        if(validateAir() && validateBase()) {
            clear()
            fillBase()
            return true
        }
        return false
    }
}