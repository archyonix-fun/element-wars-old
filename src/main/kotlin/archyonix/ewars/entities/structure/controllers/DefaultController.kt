package archyonix.ewars.entities.structure.controllers

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.events.*
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.shared.libs.getDirection
import net.minestom.server.instance.block.Block
import kotlin.math.round

class DefaultController : Structure.Config.Controller<Any>("DEFAULT") {
    init {
        addListener(StructureCalculatePositionEvent::class.java) { event ->
            event.resultPosition = event.builder.position
        }
        addListener(StructureCalculateDirectionEvent::class.java) { event ->
            event.resultDirection = event.builder.getDirection()
        }
        addListener(StructureCalculateBuildZoneEvent::class.java) { event ->
            val height = event.structureConfig.area.height
            val region2D = event.region2D
            val zeroY = event.zeroY

            event.resultAirZone = Zone(
                minX = region2D.startX,
                minY = zeroY,
                minZ = region2D.startZ,
                maxX = region2D.endX,
                maxY = zeroY + height,
                maxZ = region2D.endZ
            )

            event.resultBaseZone = Zone(
                minX = region2D.startX,
                minY = zeroY - 4,
                minZ = region2D.startZ,
                maxX = region2D.endX,
                maxY = zeroY - 1,
                maxZ = region2D.endZ
            )
        }
        addListener(StructureValidateBuildZoneEvent::class.java) { event ->
            val size = event.structureConfig.area.size
            val errorLimit = round(size * size * 0.1).toInt()
            if (!event.validateAirZone(errorLimit)) {
                event.setWarningMessage("Что-то мешает, найдите более открытое пространство")
                event.isValid = false
                return@addListener
            }
            if (!event.validateBaseZone(errorLimit)) {
                event.setWarningMessage("Что-то мешает, найдите более ровную поверхность")
                event.isValid = false
                return@addListener
            }
            event.isValid = true
        }
        addListener(StructurePrepareBuildZoneEvent::class.java) { event ->
            event.airZone.fill(Block.AIR)
            event.baseZone.fill(
                Block.GRASS_BLOCK,
                Block.GRASS_BLOCK,
                Block.COARSE_DIRT,
                Block.ROOTED_DIRT,
                Block.DIRT
            )
        }
    }

    override fun getStructureController(structure: Structure): Any =
        throw RuntimeException("StructureController has not been defined")
}