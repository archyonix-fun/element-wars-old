package archyonix.ewars.features.shop

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.functions.asGameItem
import archyonix.ewars.entities.message.Message
import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.features.shop.functions.withShopIcon
import archyonix.ewars.game
import archyonix.ewars.shared.libs.LoreUtils
import archyonix.ewars.shared.libs.loadCost
import archyonix.ewars.shared.libs.loadDependencies
import archyonix.uikit.components.UIView
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.floor

class ShopItem(
    config: ConfigurationSection
) {
    private val slot = config.name
        .replace("slot[", "")
        .replace("]", "")
        .toInt()
    private val icon = getIcon(config)
    private val cost = config.loadCost()
    private val dependencies = config.loadDependencies()
    private val actions = game.actionManager.readActions(config.getConfigurationSection("actions"))

    fun createView(player: Player): UIView {
        val team = player.gameTeam()

        return UIView("slot[$slot]")
            .withX((slot % 3) + 1)
            .withY(floor(slot / 3f).toInt() + 1)
            .withShopIcon(icon!!)
            .onRender {
                if (!checkDependencies(team)) it.withTag("texture", "HIDDEN")
                val description = ArrayList(it.description)
                description.addAll(buildLore(team))

                return@onRender it
                    .withDescription(description)
            }
            .onAction {
                if (!checkDependencies(team)) {
                    player.showActionBar(Message.NO_DEPENDENCIES, MessageType.WARNING)
                    return@onAction
                }
                /*
                  run {
                        item?.item?.union ?: return@run
                        val order = ItemVariant.valueOf(item.id).order
                        val savedItem = player.inventory.itemStacks
                            .filter { it.hasGameItem(item.item) }
                            .find { ItemVariant.valueOf(it.asItemVariant()!!.id).order >= order } != null
                        if (savedItem) {
                            player.showActionBar(Message.ALREADY_PURCHASED, MessageType.WARNING)
                            return@onAction
                        }
                */
                if (!cost.pay(player)) {
                    player.showActionBar(Message.NOT_ENOUGH_MONEY, MessageType.WARNING)
                    return@onAction
                }
                executeAction(player)
            }
    }



    private fun executeAction(player: Player) {
        actions.filter { it.triggers.contains("CLICK") }.forEach { it.execute(player) }
    }

    private fun buildLore(team: GameTeam): List<String> {
        val lore = mutableListOf<String>()
        lore.addAll(LoreUtils.buildCostLore(cost))
        lore.addAll(LoreUtils.buildDependenciesLore(dependencies))
        return lore
    }

    private fun getIcon(config: ConfigurationSection): ShopIcon? {
        if (!config.contains("icon")) return null
        val name = config.getString("icon.name")
        val description = config.getStringList("icon.description")
        val texture = config.getString("icon.texture")

        return ShopIcon(name, description, texture)
    }

    private fun checkDependencies(team: GameTeam): Boolean {
        for (dep in dependencies) {
            if (!dep.check(team)) return false
        }
        return true
    }

    data class ShopIcon(
        val name: String?,
        val description: List<String>,
        val texture: String?
    )
}