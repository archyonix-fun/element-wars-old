package archyonix.ewars.entities.player

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.asItemVariant
import archyonix.ewars.entities.item.functions.hasGameItem
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.entities.team.functions.findTeamById
import archyonix.ewars.features.pvp.PVPManager
import archyonix.ewars.game
import archyonix.ewars.shared.libs.withColors
import net.kyori.adventure.text.Component
import net.minestom.server.attribute.Attribute
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import java.util.*

enum class PlayerType { PLAYER, SPECTATOR }

val TypeTag = Tag.String("playerType")
val TeamTag = Tag.String("gameTeam")

fun Player.getRole(): PlayerType = if (hasTag(TypeTag)) PlayerType.valueOf(getTag(TypeTag)) else PlayerType.SPECTATOR
fun Player.setRole(type: PlayerType) = setTag(TypeTag, type.name)
fun Player.gameTeam(): GameTeam =
    if (hasTag(TeamTag)) findTeamById(getTag(TeamTag))!! else throw Error("Player team is null")

fun Player.setGameTeam(team: GameTeam) {
    team.minecraftTeam.addMember(username)
    team.infoBar.addViewer(this)
    setTag(TeamTag, team.id)
}

fun Player.backToSpawn() = teleport(gameTeam().spawn)
fun Player.showMessage(message: String, type: MessageType = MessageType.INFO) = sendMessage(
    type.prefix.append(message.withColors())
)

fun Player.showMessage(message: Component, type: MessageType = MessageType.INFO) = sendMessage(
    type.prefix.append(message)
)

fun Player.showActionBar(message: String, type: MessageType = MessageType.INFO) = sendActionBar(
    type.prefix.append(message.withColors())
)

fun Player.showActionBar(message: Component, type: MessageType = MessageType.INFO) = sendActionBar(
    type.prefix.append(message)
)

fun Player.addItem(item: GameItem.Variant, amount: Int = 1) {
    inventory.addItemStack(item.createItemStack(amount))
}

fun Player.updateRecharge() {
    val item = itemInMainHand.run { if (hasGameItem()) asItemVariant() else null }
    val recharge = 20f / (item?.recharge ?: 0)
    getAttribute(Attribute.ATTACK_SPEED).baseValue = recharge
}

private val coolDown: MutableMap<Player, Long> = HashMap()
fun Player.useCoolDown(): Boolean {
    val current = Date().time
    if (coolDown.containsKey(this)) {
        val last = coolDown[this]!!
        if (current - last < 100) {
            return false
        }
        coolDown[this] = current
    } else {
        coolDown[this] = current
    }
    return true
}

fun Player.getFocus() = game.pvpManager.getFocus(this)
fun Player.setFocus(focus: PVPManager.Focus) = game.pvpManager.setFocus(this, focus)