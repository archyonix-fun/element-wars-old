package archyonix.ewars.entities.ui

import archyonix.ewars.entities.player.enums.MessageType
import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.player.showActionBar
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.features.action.Action
import archyonix.ewars.features.dependencies.Dependence
import archyonix.ewars.features.shop.CostInfo
import archyonix.ewars.game
import archyonix.ewars.shared.libs.Colors
import archyonix.ewars.shared.libs.LoreUtils
import archyonix.ewars.shared.libs.withColors
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemHideFlag
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File
import kotlin.math.max
import kotlin.math.min

class UI(
    config: Config,
    val player: Player,
    val owner: Any
) : PublicEventNode("ui-handler") {
    private val inventory = Inventory(config.size, Component.text(config.name, Colors.WHITE))
    private val content: Set<UISlot> = config.content.map { UISlot(it) }.toSet()

    init {
        closeOtherUIs()
        player.openInventory(inventory); render()
        addListener(InventoryPreClickEvent::class.java) { event ->
            if (event.player.username != player.username) return@addListener
            if (event.inventory == inventory) event.isCancelled = true
            if (event.slot < 0 || event.slot >= inventory.size) return@addListener

            val sampleId = event.clickedItem.getTag(Tag.String("sampleId"))
            val slot = content.find { it.index == event.slot } ?: return@addListener
            val sample = slot.samples.find { it.id == sampleId } ?: return@addListener

            val player = event.player
            if (!sample.checkConditions(player)) return@addListener
            sample.executeActions(player, this)
        }
        addListener(InventoryCloseEvent::class.java) {
            if (it.inventory == inventory) unregister()
        }
    }

    fun close() {
        player.closeInventory()
        unregister()
    }

    private fun closeOtherUIs() {
        if (player.openInventory == null) return
        game.handler.call(InventoryCloseEvent(player.openInventory, player))
        player.closeInventory()
    }

    private fun render() {
        for (slot in content) {
            val sample = slot.samples
                .lastOrNull { it.checkConditions(player.gameTeam()) }
                ?: slot.samples
                    .firstOrNull { !it.checkConditions(player.gameTeam()) }
                ?: continue
            inventory.setItemStack(slot.index, sample.buildView(player.gameTeam()))
        }
    }

    private fun unregister() {
        game.handler.removeChild(this)
    }

    // Child classes
    class Config(
        config: ConfigurationSection
    ) {
        val name: String = config.getString("name")
            ?: throw RuntimeException("UI name is null")
        val size: InventoryType = InventoryType.valueOf(
            config.getString("size")
                ?: "CHEST_6_ROW"
        )
        val content: Set<UISlot.Config> = run {
            val content: MutableSet<UISlot.Config> = HashSet()
            val section = config.getConfigurationSection("content")
                ?: return@run content
            for (slotId in section.getKeys(false)) {
                val slotSection = section.getConfigurationSection(slotId) ?: continue
                content.add(UISlot.Config(slotSection))
            }
            return@run content
        }
    }

    class Constructor(
        path: String
    ) {
        val config: Config = run {
            val config = YamlConfiguration.loadConfiguration(File(path))
            return@run Config(config)
        }

        fun buildUI(player: Player, owner: Any): UI {
            val ui = UI(config, player, owner)
            game.handler.addChild(ui)
            return ui
        }
    }

    class UISlot(
        config: Config
    ) {
        val index = config.slot
        val samples: List<Sample> = config.samples.map { Sample(it) }

        // Child classes
        class Config(
            config: ConfigurationSection
        ) {
            private val index = config.name
                .substring(5, config.name.length - 1).toInt()
            val slot: Int = max(0, min(53, index))
            val samples: List<Sample.Config> = run {
                val samples: MutableList<Sample.Config> = ArrayList()
                for (sampleId in config.getKeys(false)) {
                    val section = config.getConfigurationSection(sampleId) ?: continue
                    samples.add(Sample.Config(section))
                }
                return@run samples
            }
        }

        class Sample(
            private val config: Config
        ) {
            val id: String = config.id
            private val view: View = View(config.view)

            fun executeActions(player: Player, ui: UI, conditions: MutableMap<String, Boolean> = mutableMapOf()) {
                val conditionMap = HashMap(conditions)

                config.actions.filter { it.trigger.contains(conditionMap) }.forEach {
                    if (!conditions.containsKey("action-${it.id}")) {
                        conditions["action-${it.id}"] = it.execute(player, ui)
                        executeActions(player, ui, conditions)
                    }
                }
            }

            fun checkConditions(gameTeam: GameTeam): Boolean {
                for (dependency in config.dependencies) {
                    if (!dependency.check(gameTeam)) return false
                }
                return true
            }

            fun checkConditions(player: Player): Boolean {
                for (dependency in config.dependencies) {
                    if (!dependency.check(player)) {
                        player.showActionBar("Не все требования выполнены", MessageType.WARNING)
                        return false
                    }
                }
                return true
            }

            fun buildView(gameTeam: GameTeam): ItemStack =
                view.buildItemStack(config, checkConditions(gameTeam))

            // Child classes
            class Config(
                config: ConfigurationSection
            ) {
                val id: String = config.name
                val view: View.Config = View.Config(
                    config.getConfigurationSection("view")
                        ?: throw RuntimeException("Sample view is null")
                )
                val cost: CostInfo = CostInfo.import(
                    config.getConfigurationSection("cost")
                ) ?: CostInfo()
                val dependencies: List<Dependence> = Dependence.import(config)
                val actions: Set<Action> = game.actionManager.readActions(
                    config.getConfigurationSection("actions")
                )
            }

            class View(
                private val config: Config
            ) {
                fun buildItemStack(sampleConfig: Sample.Config, isConditions: Boolean): ItemStack {
                    val description = ArrayList(config.description)
                    description.addAll(LoreUtils.buildCostLore(sampleConfig.cost))
                    description.addAll(LoreUtils.buildDependenciesLore(sampleConfig.dependencies))
                    return ItemStack.of(Material.STICK)
                        .withDisplayName(config.name.withColors())
                        .withLore(description.map { it.withColors() })
                        .withTag(Tag.String("sampleId"), sampleConfig.id)
                        .withTag(Tag.String("icon"), if (isConditions) config.icon else "unknown")
                        .withMeta { it.hideFlag(*ItemHideFlag.values()) }
                }

                // Child classes
                class Config(
                    config: ConfigurationSection
                ) {
                    val name: String = config.getString("name")
                        ?: throw RuntimeException("View name is null")
                    val description: List<String> = config.getStringList("description")
                    val icon: String = config.getString("icon") ?: ""
                }
            }
        }
    }
}