package archyonix.ewars.entities.item

import archyonix.ewars.entities.item.action.functions.loadActions
import archyonix.ewars.entities.item.functions.GameItemAttackSpeedTag
import archyonix.ewars.entities.item.functions.GameItemIdTag
import net.kyori.adventure.text.Component
import net.minestom.server.attribute.Attribute
import net.minestom.server.attribute.AttributeOperation
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.attribute.AttributeSlot
import net.minestom.server.item.attribute.ItemAttribute
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*
import kotlin.collections.HashMap


class GameItem(
    val id: String,
    config: ConfigurationSection
) {
    val displayName = config.getString("displayName") ?: throw RuntimeException("DisplayName is null")
    val lore = config.getStringList("lore")
    private val namespaceID = config.getString("material") ?: throw RuntimeException("Material is null")
    val attackSpeed = config.getFloat("attackSpeed")
    val material = Material.fromNamespaceId(namespaceID) ?: throw RuntimeException("Invalid material")
    private val actions = loadActions(config.getConfigurationSection("actions"))
    val data = config.getConfigurationSection("data")

    fun createItemStack(amount: Int = 1): ItemStack {
        return ItemStack.of(material, amount)
            .withDisplayName(Component.text(displayName))
            .withLore(lore.map { Component.text(it) })
            .withTag(GameItemIdTag, id)
            .withTag(GameItemAttackSpeedTag, attackSpeed)
            .withMeta {
                it.attributes(
                    listOf(
                        ItemAttribute(
                            UUID.randomUUID(),
                            Attribute.ATTACK_SPEED.key,
                            Attribute.ATTACK_SPEED,
                            AttributeOperation.ADDITION,
                            8.0, AttributeSlot.MAINHAND
                        )
                    )
                )
            }
    }

    fun executeAction(player: Player, trigger: String) {
        actions.filter { it.trigger == trigger }.forEach { it.run(player, this) }
    }
}