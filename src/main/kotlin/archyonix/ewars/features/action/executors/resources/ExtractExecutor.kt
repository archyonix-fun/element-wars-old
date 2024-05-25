package archyonix.ewars.features.action.executors.resources

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.shared.libs.Colors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.minestom.server.entity.Player
import java.util.*
import kotlin.math.round

@ClassLibrary.RegisteredClass("RESOURCE_EXTRACT", ClassLibrary.Type.ACTION_EXECUTOR)
class ExtractExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val random = Random()
    private val multiplier = (properties["multiplier"] ?: 1.0f)
        .toString().toFloat()

    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem.Variant) throw RuntimeException("Executor can only be called by a game item")
        val point = player.getTargetBlockPosition(5) ?: return false
        if (game.structureManager.findStructure3D(point) != null) return false
        val resourceSet = game.resourceManager.extract(point, caller.item.union ?: "none") ?: return false
        val storage = player.gameTeam().resources

        for (volume in resourceSet) {
            val minFarm = round(volume.min * multiplier).toInt()
            val maxFarm = round(volume.max * multiplier).toInt()
            val count = minFarm + random.nextInt(maxFarm - minFarm + 1)
            if (count <= 0) continue

            storage.add(volume.resource, count)
            player.showActionBar(
                Component.join(
                    JoinConfiguration.spaces(),
                    Component.text("Получен кристалл x$count", Colors.TEXT),
                    volume.resource.getIcon()
                )
            )
        }
        return true
    }
}