package archyonix.ewars.entities.player

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.entities.team.functions.findTeamById
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.attribute.Attribute
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
fun Player.sendInfo(message: String) = sendMessage(
    Component.text("[I]: ", TextColor.fromHexString("#0cb2aa")).append(
        Component.text(message, TextColor.fromHexString("#a2a2a2"))
    )
)
fun Player.sendWarning(message: String) = sendMessage(
    Component.text("[W]: ", TextColor.fromHexString("#d7a116")).append(
        Component.text(message, TextColor.fromHexString("#a2a2a2"))
    )
)
fun Player.sendError(message: String) = sendMessage(
    Component.text("[E]: ", TextColor.fromHexString("#c51c29")).append(
        Component.text(message, TextColor.fromHexString("#a2a2a2"))
    )
)

fun Player.addItem(item: GameItem, amount: Int = 1) {
    inventory.addItemStack(item.createItemStack(amount))
}

fun Player.updateAttackSpeed(gameItem: GameItem? = null) {
    val attackSpeed = 20f / (gameItem?.attackSpeed ?: 0f)
    getAttribute(Attribute.ATTACK_SPEED).baseValue = attackSpeed
}