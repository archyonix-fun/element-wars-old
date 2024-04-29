package archyonix.ewars.entities.item.functions

import archyonix.ewars.actionManager
import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.item.action.Action
import archyonix.ewars.entities.item.action.GameTrigger
import archyonix.ewars.game
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

fun useItemActions(item: GameItem, section: ConfigurationSection?) {
    if (section == null) return
    for (actionId in section.getKeys(false)) {
        val actionRun = actionManager.findActionRunById(actionId)
        val trigger = GameTrigger.valueOf(section.getString("$actionId.trigger")!!)
        val dataSection = section.getConfigurationSection("$actionId.data")
        val data: MutableMap<String, String> = HashMap()

        if (dataSection != null) {
            for (key in dataSection.getKeys(false)) {
                data[key] = dataSection.getString(key)!!
            }
        }

        actionManager.addAction(Action(actionRun, data, item, trigger))
    }
}

fun loadItems(): Map<String, GameItem> {
    val itemMap: MutableMap<String, GameItem> = HashMap()
    val configFile = File("config/items.yaml")
    val config = YamlConfiguration.loadConfiguration(configFile)
    for (name in config.getKeys(false)) {
        val displayName = config.getString("$name.displayName")!!
        val lore = config.getStringList("$name.lore")
        val material = config.getString("$name.material")!!
        val item = GameItem(name, displayName, lore, material)
        if(config.contains("$name.actions")) {
            useItemActions(item, config.getConfigurationSection("$name.actions"))
        }

        itemMap[name] = item
    }
    return itemMap
}

val GameItemIdTag = Tag.String("itemId")
fun ItemStack.hasGameItem() = hasTag(GameItemIdTag)

fun findItemById(id: String) = game.items[id]