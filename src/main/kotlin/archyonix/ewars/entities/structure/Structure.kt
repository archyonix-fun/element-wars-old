package archyonix.ewars.entities.structure

import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.game
import archyonix.ewars.shared.libs.getDirection
import archyonix.ewars.structureManager
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.sound.SoundEvent
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class Structure(
    val name: String,
    player: Player,
) {
    val uuid = UUID.randomUUID()
    val team = player._getTeam()
    private val position = player.position
    private val direction = player.getDirection()
    private val config = structureManager.getConfig(name)
    private val data: MutableMap<String, Any> = HashMap()
    private var variant: StructureVariant = config.variants[0]
    private var buildProgress by Delegates.notNull<Int>()
    private var successProgress by Delegates.notNull<Int>()

    val region = Region2D(position, floor(config.area / 2.0).toInt())
    private val validator = ZoneValidator(region, position.blockY(), position.blockY() + config.height)

    init {
        if (structureManager.validateRegion(this) && validator.validateAndPrepare()) {
            startBuildVariant(variant)
            structureManager.addStructure(this)
        } else throw RuntimeException("Неподходящее место. Найди другое место для строительства!")
    }

    fun getBuildProgress() = buildProgress / successProgress.toFloat()

    fun buildOrRepair(buildPower: Int) {
        if (hasBuilding()) {
            val radius = config.radius
            val position = position.add((-radius).toDouble(), .0, (-radius).toDouble())

            val newBuildProgress = max(0, min(buildProgress + buildPower, successProgress))
            val percentS = buildProgress / successProgress.toFloat()
            val percentE = newBuildProgress / successProgress.toFloat()

            variant.schema.build(
                game.instance, direction, position, team, percentS, percentE, useEffects = true
            )

            this.buildProgress = newBuildProgress
            if (!hasBuilding()) {
                val sound = Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1f)
                game.instance.playSound(sound, this.position)
            }
        }
    }

    private fun startBuildVariant(variant: StructureVariant) {
        this.variant = variant
        buildProgress = 0
        successProgress = variant.buildPoints
        validator.clear()

        placeBlueprint()
    }

    private fun placeBlueprint() {
        val radius = config.radius
        val position = position.add((-radius).toDouble(), .0, (-radius).toDouble())
        variant.schema.fillWithOutAir(game.instance, position, direction, Block.SCAFFOLDING)
    }

    private fun getPlayersFromRadius(radius: Double) =
        game.getPlayers().filter { it.position.distance(position) <= radius }

    fun hasBuilding() = buildProgress < successProgress
}