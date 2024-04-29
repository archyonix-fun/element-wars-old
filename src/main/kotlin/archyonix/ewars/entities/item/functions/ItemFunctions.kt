package archyonix.ewars.entities.item.functions

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.game
import archyonix.ewars.shared.libs.toStringMap
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

fun useItemActions(item: GameItem, section: ConfigurationSection?) {
    if (section == null) return
    for (actionId in section.getKeys(false)) {
        val dataSection = section.getConfigurationSection("$actionId.data")
        val triggerDataSection = section.getConfigurationSection("$actionId.trigger.data")
        val data = dataSection!!.toStringMap()
        val triggerData = triggerDataSection!!.toStringMap()
    }
}

fun loadItems(): Map<String, GameItem> {
    val itemMap: MutableMap<String, GameItem> = HashMap()
    val configFile = File("config/items.yaml")
    val config = YamlConfiguration.loadConfiguration(configFile)
    for (name in config.getKeys(false)) {
        val section = config.getConfigurationSection(name)!!
        itemMap[name] = GameItem(name, section)
    }
    return itemMap
}

val GameItemIdTag = Tag.String("itemId")
fun ItemStack.hasGameItem() = hasTag(GameItemIdTag)
fun ItemStack.getGameItemId(): String = getTag(GameItemIdTag)

fun findItemById(id: String) = game.items[id]