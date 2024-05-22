package archyonix.ewars.entities.structure.controllers

import archyonix.ewars.entities.region.Zone
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.events.*
import archyonix.ewars.shared.libs.getDirection
import net.minestom.server.instance.block.Block
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.round

class DefaultController(
    structure: Structure,
    config: Config
) : Structure.Controller<DefaultController, DefaultController.Config>(structure, config) {
    // Child classes
    class Config(
        val config: ConfigurationSection? = null
    ) : Structure.Controller.Config<DefaultController, Config>("DEFAULT") {

        init {
            priority = -1
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

                event.resultAirZone = Zone(region2D, zeroY, zeroY + height)
                event.resultBaseZone = Zone(region2D, zeroY - 4, zeroY - 1)
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

        override fun getController(structure: Structure): DefaultController =
            DefaultController(structure, this)
    }
}