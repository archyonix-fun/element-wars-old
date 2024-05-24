package archyonix.ewars.features.action.executors.structure

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.game.functions.ActionBar
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent

@ClassLibrary.RegisteredClass("STRUCTURE_BUILD", ClassLibrary.Type.ACTION_EXECUTOR)
class BuildExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    private val buildPower = (properties["buildPower"] ?: 0) as Int
    override fun run(player: Player, caller: Any): Boolean {
        if (caller !is GameItem.Variant) throw RuntimeException("Executor can only be called by the game item")
        if (!player.itemInMainHand.hasCharged()) return false

        val point = player.getTargetBlockPosition(5) ?: return false
        val structure = game.structureManager.findStructure3D(point) ?: return false
        if (structure.team != player.gameTeam()) return false

        if (structure.getProcess().type != Structure.Process.Type.IDLE) {
            structure.build(buildPower)
            ActionBar(ActionBar.Progress(structure.getProcess().getProgress())).show(player)

            if (structure.getProcess().type == Structure.Process.Type.IDLE) {
                val sound = Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1f)
                game.instance.playSound(sound, structure.position)
            }
            return true
        }
        return false
    }
}