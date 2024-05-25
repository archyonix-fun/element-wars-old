package archyonix.ewars.features.action.executors.structure

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import kotlin.math.round

@ClassLibrary.RegisteredClass("STRUCTURE_SCHEMA_SELL", ClassLibrary.Type.ACTION_EXECUTOR)
class SchemaSellExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem.Variant) throw RuntimeException("Executor can only be called by a game item")
        if (caller.item != GameItem.STRUCTURE_SCHEMA) return false

        val item = player.itemInMainHand
        val structureId = item.getTag(Tag.String("structureId"))

        val storage = player.gameTeam().resources
        val cost = game.structureManager.getConfig(structureId).defaultVariant.cost

        player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_ATTACK_NODAMAGE, Sound.Source.PLAYER, 1f, 1f))
        player.showActionBar("Продано!")
        for (resource in cost.resources.keys) {
            val count = round(cost.resources[resource]!! / 2.0).toInt()
            storage.add(resource, count)
        }
        return true
    }
}