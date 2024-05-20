package archyonix.ewars.features.action.executors

import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.hasCharged
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import archyonix.ewars.game.functions.ActionBar
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent

@ClassLibrary.RegisteredClass("BUILD/REPAIR", ClassLibrary.Type.EXECUTOR)
class BuildExecutor(
    properties: Map<String, Any>
) : Action.Executor(properties) {
    override fun run(player: Player): Boolean {
        val itemStack = player.itemInMainHand
        if (!itemStack.hasCharged()) return false

        val variant = player.itemInMainHand.asItemVariant() ?: return false
        val point = player.getTargetBlockPosition(5) ?: return false
        val structure = game.structureManager.findStructure3D(point) ?: return false
        if (structure.team != player.gameTeam()) return false

        val buildPower = (variant.properties["buildPower"] ?: 0) as Int

        if(!structure.getProcess().hasFinished()) {
            structure.build(buildPower)
            ActionBar(ActionBar.Progress(structure.getProcess().getProgress())).show(player)

            if (structure.getProcess().hasFinished()) {
                val sound = Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1f)
                game.instance.playSound(sound, structure.position)
            }
        }
        return true
    }
}