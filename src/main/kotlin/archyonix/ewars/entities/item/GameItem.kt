package archyonix.ewars.entities.item

import archyonix.ewars.entities.item.enums.ItemFlag
import archyonix.ewars.entities.item.enums.ItemVariant
import archyonix.ewars.entities.item.functions.GameItemIdTag
import archyonix.ewars.entities.item.functions.GameItemRechargeTag
import archyonix.ewars.entities.item.functions.GameItemIconTag
import archyonix.ewars.entities.item.functions.GameItemVariantTag
import archyonix.ewars.entities.player.getConditions
import archyonix.ewars.features.action.Action
import archyonix.ewars.game
import archyonix.ewars.shared.libs.withColors
import net.minestom.server.attribute.Attribute
import net.minestom.server.attribute.AttributeOperation
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemHideFlag
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.attribute.AttributeSlot
import net.minestom.server.item.attribute.ItemAttribute
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File
import java.lang.reflect.Modifier
import java.util.*
import kotlin.collections.HashSet

class GameItem private constructor(
    config: ConfigurationSection
) {
    companion object {
        private const val configDir = "config/items"

        /* Items */
        val CONSTRUCTION_HAMMER = loadByName("construction_hammer")
        val FISHING_ROD = loadByName("fishing_rod")
        val PICKAXE = loadByName("pickaxe")
        val AXE = loadByName("axe")
        val STRUCTURE_SCHEMA = loadByName("structure_schema")

        fun items(): Set<GameItem> =
            GameItem::class.java
                .declaredFields
                .filter { Modifier.isStatic(it.modifiers) }
                .filter { it.name != "Companion" }
                .filter { it.type.name == GameItem::class.java.name }
                .map {
                    it.isAccessible = true
                    it.get(null) as GameItem
                }.toSet()

        private fun loadByName(name: String): GameItem {
            val configPath = "$configDir/$name.yaml"
            val config = YamlConfiguration
                .loadConfiguration(File(configPath))
            return GameItem(config)
        }
    }

    val id: String = config.getString("id") ?: throw RuntimeException("Field id is null")
    val union: String? = config.getString("union")
    private val default = loadDefault(config.getConfigurationSection("default"))
    private val variants = loadVariants(config.getConfigurationSection("variants"))

    fun getVariant(variant: ItemVariant) =
        variants.find { it.id == variant.name }

    fun getVariant(variant: String) =
        variants.find { it.id == variant }

    private fun loadDefault(section: ConfigurationSection?): Variant? =
        if (section == null) null else Variant(this, section)

    private fun loadVariants(section: ConfigurationSection?): Set<Variant> {
        if (section == null) throw RuntimeException("Variants in null")
        return section.getKeys(false)
            .map { Variant(this, section.getConfigurationSection(it)!!, default) }
            .toSet()
    }

    class Variant(
        val item: GameItem,
        config: ConfigurationSection,
        default: Variant? = null
    ) {
        val id: String = config.name
        val order = ItemVariant.valueOf(id.uppercase()).order
        val name: String? = config.getString("name") ?: default?.name
        private val description: List<String> = config.contains("description").run {
            val description = config.getStringList("description")
            description.addAll(default?.description ?: emptyList())

            if (this) return@run description
            return@run default?.description ?: emptyList()
        }
        val properties: Map<String, Any> = config.contains("properties").run {
            val defaultProperties: MutableMap<String, Any> = HashMap(default?.properties ?: mutableMapOf())
            if (this) {
                val section = config.getConfigurationSection("properties")!!
                defaultProperties.putAll(
                    section.getKeys(false).associateWith {
                        section.get(it)!!
                    }
                )
            }
            return@run defaultProperties
        }
        val flags: Set<ItemFlag> = config.contains("flags").run {
            if (this) return@run config.getStringList("flags").map { ItemFlag.valueOf(it) }.toSet()
            return@run default?.flags ?: emptySet()
        }
        private val icon: String? = config.getString("icon") ?: default?.icon
        val recharge: Int = config.contains("recharge").run {
            if (this) return@run config.getInt("recharge")
            return@run default?.recharge ?: 0
        }
        private val namespaceID: String? = config.getString("material") ?: default?.namespaceID
        private val material: Material? =
            if (namespaceID == null) null
            else Material.fromNamespaceId(namespaceID)
        private val actions: Set<Action> = run {
            val section = config.getConfigurationSection("actions")
            val actions = (default?.actions ?: HashSet()) as MutableSet<Action>
            actions.addAll(game.actionManager.readActions(section))
            return@run actions
        }

        fun executeAction(player: Player, conditions: MutableMap<String, Boolean> = mutableMapOf()) {
            val conditionMap = HashMap(player.getConditions())
            conditionMap.putAll(conditions)

            actions.filter { it.trigger.contains(conditionMap) }.forEach {
                if(!conditions.containsKey("action-${it.id}")) {
                    conditions["action-${it.id}"] = it.execute(player, this)
                    executeAction(player, conditions)
                }
            }
        }

        fun createItemStack(amount: Int = 1) = ItemStack.of(material ?: Material.AIR, amount)
            .withDisplayName(name?.withColors())
            .withLore(description.map { it.withColors() })
            .withTag(GameItemIdTag, item.id)
            .withTag(GameItemVariantTag, id)
            .withTag(GameItemIconTag, icon)
            .withTag(GameItemRechargeTag, recharge.toFloat())
            .withMeta {
                it.attributes(
                    listOf(
                        ItemAttribute(
                            UUID.fromString("d541bf47-0e97-4591-984e-9f0882d75d98"),
                            Attribute.ATTACK_SPEED.key,
                            Attribute.ATTACK_SPEED,
                            AttributeOperation.ADDITION,
                            8.0, AttributeSlot.MAINHAND
                        )
                    )
                ).hideFlag(
                    ItemHideFlag.HIDE_UNBREAKABLE,
                    ItemHideFlag.HIDE_ATTRIBUTES,
                    ItemHideFlag.HIDE_ENCHANTS,
                    ItemHideFlag.HIDE_POTION_EFFECTS,
                )
            }

    }
}