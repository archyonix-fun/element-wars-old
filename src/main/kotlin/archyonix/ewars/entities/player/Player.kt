package archyonix.ewars.entities.player

import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag

enum class PlayerType { PLAYER, SPECTATOR }

val TypeTag = Tag.String("playerType")

fun Player.getType(): PlayerType = if (hasTag(TypeTag)) PlayerType.valueOf(getTag(TypeTag)) else PlayerType.SPECTATOR
fun Player.setType(type: PlayerType) = setTag(TypeTag, type.name)