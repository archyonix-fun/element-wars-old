package archyonix.ewars.game.task

import archyonix.ewars.game
import archyonix.ewars.game.functions.ActionBar
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.shared.libs.Icons
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.other.FishingHookMeta
import net.minestom.server.instance.block.Block
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import java.time.Duration
import java.util.*
import kotlin.math.min

class FishingTask : Runnable {
    private val fishingTime = 10000f
    override fun run() {
        val current = Date().time
        game.instance.entities
            .filter { it.entityType == EntityType.FISHING_BOBBER }
            .filter { it.instance.getBlock(it.position).name() == Block.WATER.name() }
            .forEach {
                val spawnTime = it.getTag(Tag.Long("spawnTime"))
                it.velocity = Vec.ZERO
                val player = (it.entityMeta as FishingHookMeta).ownerEntity as Player

                val percent = min((current - spawnTime) / fishingTime, 1f)
                if (percent < 1f) ActionBar(ActionBar.Progress(percent)).show(player)
                else {
                    player.sendActionBar(
                        Component.join(
                            JoinConfiguration.spaces(),
                            Icons.WATER_CRYSTAL,
                            Component.text("ТЯНИ!", Colors.TEXT),
                            Icons.WATER_CRYSTAL,
                        )
                    )
                    if (!it.hasTag(Tag.Boolean("isSuccess"))) playSound(player)
                    it.setTag(Tag.Boolean("isSuccess"), true)
                }
            }

        game.schedule.buildTask(this).delay(Duration.ofMillis(500)).schedule()
    }

    private fun playSound(player: Player) {
        val sound = Sound.sound(SoundEvent.ENTITY_FISHING_BOBBER_SPLASH, Sound.Source.PLAYER, 1f, 1f)
        player.playSound(sound, player.position)
    }
}