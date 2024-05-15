package archyonix.ewars.entities.structure

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.features.shop.ShopItem
import archyonix.ewars.shared.libs.loadCost
import archyonix.ewars.shared.libs.loadDependencies
import archyonix.ewars.structureManager
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.floor

class StructureConfig(
    private val config: ConfigurationSection
) {
    val name = config.getString("name") ?: throw RuntimeException("Name is null")
    val limit = config.getInt("limit")
    val height = config.getInt("height")
    val area = config.getInt("area")
    val radius = floor(area / 2.0).toInt()
    val tagList = config.getStringList("tags")
    private val uiClass = config.getString("ui")
    private val variantSection = config.getConfigurationSection("variants")
        ?: throw RuntimeException("Variants is null: $name")
    val variants = loadVariants()
    private var shopItems: List<ShopItem>? = null

    fun shopItems(): List<ShopItem> {
        if (shopItems == null) {
            shopItems = loadShopItems()
        }
        return shopItems!!
    }


    fun checkLimit(team: GameTeam): Boolean {
        if (limit == 0) return true
        return structureManager.structureSet
            .filter { it.team == team }
            .filter { it.name == name }
            .size < limit
    }

    fun getUIClass() = structureManager.findUI(uiClass)

    private fun loadVariants(): MutableList<StructureVariant> {
        val variants: MutableList<StructureVariant> = ArrayList()
        for (variantName in variantSection.getKeys(false)) {
            val variantConfig = variantSection.getConfigurationSection(variantName)!!
            variants.add(StructureVariant(name, variantName, variantConfig))
        }
        if (variants.isEmpty()) throw RuntimeException("Variants is empty: $name")
        return variants
    }

    private fun loadShopItems(): MutableList<ShopItem> {
        val shopItems = mutableListOf<ShopItem>()
        val section = config.getConfigurationSection("shopItems") ?: return shopItems
        for (itemId in section.getKeys(false)) {
            val itemConfig = section.getConfigurationSection(itemId) ?: continue
            val gameItem = GameItem.items().find { it.id == itemId } ?: continue
            val amount = itemConfig.getInt("amount", 1)
            val variantId = itemConfig.getString("variant") ?: "DEFAULT"

            val itemVariant = gameItem.getVariant(variantId) ?: continue
            val dependencies = itemConfig.loadDependencies()
            val costInfo = itemConfig.loadCost()
            shopItems.add(ShopItem(itemVariant, amount, dependencies, costInfo))
        }
        return shopItems
    }
}