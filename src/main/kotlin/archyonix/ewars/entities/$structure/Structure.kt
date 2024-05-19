package archyonix.ewars.entities.`$structure`

import archyonix.ewars.entities.structure.Dependence
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.features.shop.CostInfo
import archyonix.ewars.game
import archyonix.ewars.shared.libs.AppSchema
import net.minestom.server.coordinate.Point
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.PublicEventNode
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class Structure(
    val id: String,
    val position: Point,
    val direction: Direction,
    val team: GameTeam
) {

    class Config(
        config: ConfigurationSection
    ) {
        val handler: EventNode<Event> = EventNode.all("structure-handler")
        val id: String = config.getString("id")
            ?: throw RuntimeException("Field name is null")
        val area: Area = Area(
            config.getConfigurationSection("area")
                ?: throw RuntimeException("Field area is null")
        )
        val buildingLimit: Int = config.getInt("buildingLimit", 0)
        private val defaultSettings: DefaultSettings =
            DefaultSettings(config.getConfigurationSection("defaultSettings"))
        val variants: List<Variant> = run {
            val section = config.getConfigurationSection("variants")
                ?: throw RuntimeException("Field variants is null")
            val variantList: MutableList<Variant> = ArrayList()
            for (id in section.getKeys(false)) {
                val variantConfig = section.getConfigurationSection(id) ?: continue
                variantList.add(Variant(variantConfig, defaultSettings))
            }
            return@run variantList
        }

        init {
            game.handler.addChild(handler)
        }
        // Functions

        fun checkBuildingLimit(team: GameTeam): Boolean {
            return if (buildingLimit == 0) true
            else game.structureManager.findStructures(team, id).size < buildingLimit
        }

        // Child Classed
        class Area(
            config: ConfigurationSection
        ) {
            val size: Int = config.getInt("size")
            val height: Int = config.getInt("height")
        }

        class DefaultSettings(
            config: ConfigurationSection?
        ) {
            val name: String? = config?.getString("name")
            val schema: String? = config?.getString("schema")
            val cost: CostInfo? = CostInfo
                .import(config?.getConfigurationSection("cost"))
            val dependencies: List<Dependence>? = Dependence
                .import(config?.getConfigurationSection("dependencies"))
            val buildPoints: Int = config?.getInt("buildPoints", -1) ?: -1
            val properties: Map<String, Any> = run {
                val section = config?.getConfigurationSection("properties")
                    ?: return@run emptyMap()
                val properties: MutableMap<String, Any> = HashMap()
                for (key in section.getKeys(false)) {
                    properties[key] = section.get(key)
                        ?: continue
                }
                return@run properties
            }
        }

        class Variant(
            config: ConfigurationSection,
            defaultSettings: DefaultSettings
        ) {
            val name: String = config.getString("name")
                ?: defaultSettings.name
                ?: throw RuntimeException("Field name is null")
            val schema: AppSchema = AppSchema(
                config.getString("schema")
                    ?: defaultSettings.schema
                    ?: throw RuntimeException("Field schema is null")
            )
            val cost: CostInfo = CostInfo
                .import(config.getConfigurationSection("cost"))
                ?: defaultSettings.cost
                ?: CostInfo()
            val dependencies: List<Dependence> = Dependence
                .import(config.getConfigurationSection("dependencies"))
                ?: defaultSettings.dependencies
                ?: emptyList()
            val buildPoints: Int = config
                .getInt("buildPoints", defaultSettings.buildPoints)
        }

        abstract class Controller(
            config: ConfigurationSection
        ) : PublicEventNode("structure-controller")
    }
}