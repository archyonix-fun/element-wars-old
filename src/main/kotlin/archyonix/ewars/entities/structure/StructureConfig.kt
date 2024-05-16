package archyonix.ewars.entities.structure

import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.features.shop.ShopItem
import archyonix.ewars.structureManager
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File
import kotlin.math.floor

class StructureConfig(
    config: ConfigurationSection
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
    var uiName: String = "NaN"
    private var shopItems: List<ShopItem>? = null
    fun shopItems(): List<ShopItem> {
        if (shopItems == null) shopItems = loadShop()
        return shopItems!!
    }

    fun hasShop() = shopItems().isNotEmpty()


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

    private fun loadShop(): MutableList<ShopItem> {
        val configPath = "config/structures/$name/shop.yaml"
        val items: MutableList<ShopItem> = ArrayList()

        val file = File(configPath)
        if (!file.exists()) return items
        val config = YamlConfiguration.loadConfiguration(file)

        this.uiName = config.getString("name") ?: uiName
        val itemSection = config.getConfigurationSection("items") ?: return items
        for (id in itemSection.getKeys(false)) {
            val section = itemSection.getConfigurationSection(id) ?: continue
            items.add(ShopItem(section))
        }

        return items
    }
}