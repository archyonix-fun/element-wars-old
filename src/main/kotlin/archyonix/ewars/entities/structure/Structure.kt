package archyonix.ewars.entities.structure

import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.entities.player.sendError
import archyonix.ewars.entities.player.sendInfo
import archyonix.ewars.entities.player.sendWarning
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.game
import archyonix.ewars.shared.libs.firstFromTop
import archyonix.ewars.shared.libs.getDirection
import archyonix.ewars.structureManager
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.Direction
import java.util.*
import kotlin.math.floor

abstract class Structure<Variant : Enum<*>>(
    typeId: String,
    defaultVariant: Variant,
    val player: Player,
) {
    val config: StructureData = structureManager.configByTypeId(typeId)
    val direction: Direction = player.getDirection()
    val pos: Point = player.position.firstFromTop()

    val id: UUID = UUID.randomUUID()
    val team: GameTeam = player._getTeam()
    val data: MutableMap<String, String> = HashMap()
    private var currentVariant: Variant? = null

    init {
        buildVariant(defaultVariant)
    }

    fun getRegion(): Region2D {
        val radius = floor(config.area / 2.0).toInt()
        return Region2D(pos, radius)
    }

    fun buildVariant(variant: Variant) {
        val builder = StructureBuilder(team, this, variant)
        if (currentVariant == null) {
            if (
                !structureManager.validateRegion(this)
                ||
                !builder.validateBase()
                ||
                !builder.validateAir()) {
                player.sendWarning("Неподходящее место, найди что-то по ровнее и просторнее")
                return
            }
        }
        builder.clearAir()
        builder.fillBase()
        if (!builder.placeBlueprint()) {
            player.sendError("Ошибка строительства")
            return
        }
        currentVariant = variant
        structureManager.addStructure(this)
        Thread {
            while (builder.addProgress(1)) Thread.sleep(1000)
            val sound = Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1f)
            game.getPlayers().forEach { it.playSound(sound, pos) }
            player.sendInfo("Готово")
        }.start()
    }
}