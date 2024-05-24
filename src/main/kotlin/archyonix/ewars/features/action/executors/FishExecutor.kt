package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.resource.events.SuccessFishingEvent
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.other.FishingHookMeta
import net.minestom.server.tag.Tag
import java.util.*

@ClassLibrary.RegisteredClass("FISHING_ROD", ClassLibrary.Type.ACTION_EXECUTOR)
class FishExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    companion object {
        val SUCCESS_TAG = Tag.Boolean("isSuccess")
        val SPAWN_TIME_TAG = Tag.Long("spawnTime")
    }
    override fun run(player: Player, caller: Any): Boolean {
        if(caller !is GameItem) throw RuntimeException("Executor can only be called by a game item")
        if(caller != GameItem.FISHING_ROD) throw RuntimeException("Executor can only be called with a fishing rod")
        val fishingBobber = game.instance.entities
            .filter { it.entityType == EntityType.FISHING_BOBBER }
            .find { (it.entityMeta as FishingHookMeta).ownerEntity == caller }

        if (fishingBobber != null) {
            val isSuccess = fishingBobber.hasTag(SUCCESS_TAG)
            if (isSuccess) game.handler.call(SuccessFishingEvent(player))
            fishingBobber.remove()
        } else spawnFishingBobber(player)
        return true
    }

    private fun spawnFishingBobber(player: Player) {
        val fishingBobber = Entity(EntityType.FISHING_BOBBER)
        fishingBobber.velocity = player.position.add(.0, player.eyeHeight, .0).direction().normalize().mul(20.0)
        fishingBobber.setTag(SPAWN_TIME_TAG, Date().time)
        fishingBobber.setInstance(player.instance, player.position.add(.0, 1.2, .0))
        val meta = fishingBobber.entityMeta as FishingHookMeta
        meta.ownerEntity = player
    }
}