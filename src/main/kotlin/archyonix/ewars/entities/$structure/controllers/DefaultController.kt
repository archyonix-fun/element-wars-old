package archyonix.ewars.entities.`$structure`.controllers

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.entities.`$structure`.events.*
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.shared.libs.getDirection
import net.minestom.server.instance.block.Block
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.round

class DefaultController : Structure.Config.Controller(null) {
    init {
        addListener(CalculateStructurePositionEvent::class.java) { event ->
            event.resultPosition = event.builder.position
        }
        addListener(CalculateStructureDirectionEvent::class.java) { event ->
            event.resultDirection = event.builder.getDirection()
        }
        addListener(CalculateBuildZoneEvent::class.java) { event ->
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
        addListener(ValidateBuildZoneEvent::class.java) { event ->
            val size = event.structureConfig.area.size
            val errorLimit = round(size * size * 0.1).toInt()
            if (!event.validateAirZone(errorLimit)) {
                event.setErrorMessage("Что-то мешает, найдите более открытое пространство")
                event.isValid = false
                return@addListener
            }
            if (!event.validateBaseZone(errorLimit)) {
                event.setErrorMessage("Что-то мешает, найдите более ровную поверхность")
                event.isValid = false
                return@addListener
            }
            event.isValid = true
        }
        addListener(PrepareBuildZoneEvent::class.java) { event ->
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
}