package archyonix.ewars.entities.item.action.executors

import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.features.mine_water.events.SuccessFishingEvent
import archyonix.ewars.game
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.other.FishingHookMeta
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*


@Executor("FISHING")
class FishingExecutor : IExecutor {
    override fun execute(player: Player, data: ConfigurationSection): Boolean {
        game.instance.entities
            .filter { it.entityType == EntityType.FISHING_BOBBER }
            .find { (it.entityMeta as FishingHookMeta).ownerEntity == player }
            ?.run {
                val isSuccess = hasTag(Tag.Boolean("isSuccess"))
                if (isSuccess) MinecraftServer.getGlobalEventHandler().call(SuccessFishingEvent(player))

                remove()
                return true
            }

        spawnBobber(player)
        return true
    }

    private fun spawnBobber(player: Player): Entity {
        val bobber = Entity(EntityType.FISHING_BOBBER)
        bobber.velocity = player.position.add(.0, player.eyeHeight, .0).direction().normalize().mul(20.0)
        bobber.setTag(Tag.Long("spawnTime"), Date().time)
        val meta = bobber.entityMeta as FishingHookMeta
        meta.ownerEntity = player
        bobber.setInstance(player.instance, player.position.add(.0, 1.2, .0))
        return bobber
    }
}