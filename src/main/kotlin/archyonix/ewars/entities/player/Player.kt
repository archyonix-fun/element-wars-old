package archyonix.ewars.entities.player

import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.entities.team.functions.findTeamById
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag

enum class PlayerType { PLAYER, SPECTATOR }

val TypeTag = Tag.String("playerType")
val TeamTag = Tag.String("gameTeam")

fun Player._getType(): PlayerType = if (hasTag(TypeTag)) PlayerType.valueOf(getTag(TypeTag)) else PlayerType.SPECTATOR
fun Player._setType(type: PlayerType) = setTag(TypeTag, type.name)
fun Player._getTeam(): GameTeam =
    if (hasTag(TeamTag)) findTeamById(getTag(TeamTag))!! else throw Error("Player team is null")

fun Player._setTeam(team: GameTeam) {
    team.minecraftTeam.addMember(username)
    setTag(TeamTag, team.id)
}

fun Player.backToSpawn() = teleport(_getTeam().spawn)