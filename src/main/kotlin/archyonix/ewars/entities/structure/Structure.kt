package archyonix.ewars.entities.structure

import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.region.Region3D
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.entities.structure.controllers.DefaultController
import archyonix.ewars.entities.structure.events.*
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.features.shop.CostInfo
import archyonix.ewars.features.shop.Dependence
import archyonix.ewars.game
import archyonix.ewars.shared.libs.AppSchema
import archyonix.ewars.shared.libs.config.Properties
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.PublicEventNode
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Direction
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Structure(
    val id: String,
    val position: Point,
    val direction: Direction,
    val team: GameTeam,
    private val buildZone: Zone,
) : Properties() {
    val config = game.structureManager.getConfig(id)
    val region3D = Region3D(position, config.area)
    val region2D = Region2D(position, config.area)
    val buildPosition = region3D.startPoint()
    private var currentVariant: Config.Variant? = null
    private var process: Process = Process(Process.Type.NONE)

    // Functions
    fun changeVariant(variantId: String): Boolean {
        val foundedVariant = config.variants.find { it.id == variantId }
            ?: throw RuntimeException("Variant with id `$variantId` is not exists")
        return changeVariant(foundedVariant)
    }

    fun changeVariant(variant: Config.Variant): Boolean {
        if (!config.variants.contains(variant))
            throw RuntimeException("The transmitted variant `${variant.id}` is not intended for this structure")

        if (process.type != Process.Type.IDLE && process.type != Process.Type.NONE) return false
        val processType = if (currentVariant == null) Process.Type.BUILDING else Process.Type.UPGRADE
        val changeVariantEvent = StructureChangeVariantEvent(this, variant)
        config.handler.call(changeVariantEvent)
        currentVariant = changeVariantEvent.resultVariant

        buildZone.fill(Block.AIR)
        process = Process(processType, currentVariant!!.buildPoints, 0)
        currentVariant?.installBlueprint(this)
        return true
    }

    fun build(value: Int): Boolean {
        if (process.hasFinished()) return false
        val startProgress = process.getProgress()

        val buildEvent = StructureBuildEvent(this, value)
        config.handler.call(buildEvent)
        process.addProgress(buildEvent.buildPoints)

        return if (startProgress < process.getProgress()) {
            if (process.hasFinished()) process = Process(Process.Type.IDLE)
            build(startProgress)
            true
        } else false
    }

    private fun build(startProgress: Float) {
        currentVariant?.schema?.build(
            game.instance, direction, buildPosition, team,
            startProgress, process.getProgress(),
            useEffects = true
        )
    }

    fun deconstruct(): Boolean {
        val deconstructEvent = StructureDeconstructEvent(this)
        config.handler.call(deconstructEvent)
        if (deconstructEvent.isCancelled) return false
        buildZone.fill(Block.AIR)
        return true
    }

    fun update() = config.handler.call(StructureUpdateEvent(this))

    fun getVariant() = currentVariant
    fun getProcess() = process

    // Child classed
    class Config(
        config: ConfigurationSection
    ) {
        val handler: EventNode<Event> = EventNode.all("structure-handler")
        val id: String = config.getString("id")
            ?: throw RuntimeException("Field id is null")
        val grid: Int = config.getInt("grid", 1)
        val area: Area = Area(
            config.getConfigurationSection("area")
                ?: throw RuntimeException("Field area is null")
        )
        val buildingLimit: Int = config.getInt("buildingLimit", 0)
        private val defaultSettings: DefaultSettings =
            DefaultSettings(config.getConfigurationSection("defaultSettings"))
        val controllers: Set<Controller.Config<*, *>> = run {
            val section = config.getConfigurationSection("controllers")
                ?: return@run setOf(DefaultController.Config())
            val controllerSet: MutableSet<Controller.Config<*, *>> = HashSet()
            for (id in section.getKeys(false)) {
                val controllerConfig = section.getConfigurationSection(id) ?: continue
                val controllerConstructor = game.classLibrary
                    .findConstructor<Controller.Config<*, *>>(id, ClassLibrary.Type.STRUCTURE_CONTROLLER)
                    ?: throw RuntimeException("Controller config with id `$id` is not found")
                val controller = controllerConstructor.newInstance(controllerConfig)
                val dependencies = controller.dependencies.joinToString(", ")
                if (!controller.checkDependencies(controllerSet))
                    throw RuntimeException("Necessary dependencies for the controller are missing: (${dependencies})")
                controllerSet.add(controller)
            }
            controllerSet.add(DefaultController.Config())

            return@run controllerSet
        }
        val variants: List<Variant> = run {
            val section = config.getConfigurationSection("variants")
                ?: throw RuntimeException("Field variants is null")
            val variantList: MutableList<Variant> = ArrayList()
            for (id in section.getKeys(false)) {
                val variantConfig = section.getConfigurationSection(id) ?: continue
                variantList.add(Variant(this.id, variantConfig, defaultSettings))
            }
            return@run variantList
        }

        val defaultVariant = run {
            if (variants.isEmpty()) throw RuntimeException("Field variants is empty")
            return@run variants[0]
        }

        init {
            for (controller in controllers) handler.addChild(controller)
            game.handler.addChild(handler)
        }

        fun init(): Config {
            controllers.forEach { it.init(this) }
            return this
        }

        // Functions
        @Suppress("UNCHECKED_CAST")
        fun <T : Controller.Config<*, *>> getController(configClass: Class<T>): T? {
            val id = configClass.getAnnotation(ClassLibrary.RegisteredClass::class.java).name
            return controllers
                .filter { it.javaClass.isAnnotationPresent(ClassLibrary.RegisteredClass::class.java) }
                .find { it.javaClass.getAnnotation(ClassLibrary.RegisteredClass::class.java).name == id } as T?
        }

        fun hasController(configClass: Class<Controller.Config<*, *>>): Boolean {
            return getController(configClass) != null
        }

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
            val radius: Int = floor(size / 2.0).toInt()
            val isEven: Boolean = size % 2 == 0
        }

        class LocalPosition(
            config: ConfigurationSection
        ) {
            val x: Int = config.getInt("x")
            val y: Int = config.getInt("y")
            val z: Int = config.getInt("z")

            fun asPoint(): Point = Pos(
                x.toDouble(),
                y.toDouble(),
                z.toDouble()
            )
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
            structureId: String,
            config: ConfigurationSection,
            defaultSettings: DefaultSettings
        ) : Properties(config) {
            val id: String = config.name
            val name: String = config.getString("name")
                ?: defaultSettings.name
                ?: throw RuntimeException("Field name is null")
            val schema: AppSchema = run {
                val localPath = config.getString("schema")
                    ?: defaultSettings.schema
                    ?: throw RuntimeException("Field schema is null")
                AppSchema("config/structures/$structureId/schemas/$localPath")
            }
            val cost: CostInfo = CostInfo
                .import(config.getConfigurationSection("cost"))
                ?: defaultSettings.cost
                ?: CostInfo()
            val dependencies: List<Dependence> = Dependence.import(config)
                ?: defaultSettings.dependencies
                ?: emptyList()
            val buildPoints: Int = config
                .getInt("buildPoints", defaultSettings.buildPoints)

            fun installBlueprint(structure: Structure) {
                schema.fillWithOutAir(game.instance, structure.buildPosition, structure.direction, Block.SCAFFOLDING)
            }

            fun checkDependencies(team: GameTeam): Boolean {
                for (dependency in dependencies) {
                    if (!dependency.check(team)) return false
                }
                return true
            }
        }
    }

    abstract class Controller<C : Controller<C, T>, T : Controller.Config<C, T>>(
        val structure: Structure,
        val config: T
    ) {

        // Child classes
        abstract class Config<C : Controller<C, T>, T : Config<C, T>>(
            val id: String,
            val dependencies: Set<String> = emptySet()
        ) : PublicEventNode("structure-controller") {
            open fun init(structureConfig: Structure.Config) {}
            fun checkDependencies(controllerList: Set<Config<*, *>>): Boolean {
                val controllers = controllerList.map { it.id }.toSet()
                return controllers.containsAll(dependencies)
            }

            abstract fun getController(structure: Structure): C
        }
    }

    class Process(
        val type: Type,
        private var maxValue: Int = 0,
        private var currentValue: Int = 0
    ) {
        private val startTime = Date().time + 0
        private var updateTime = Date().time + 0

        fun addProgress(value: Int) {
            currentValue = min(currentValue + value, maxValue)
        }

        fun subProgress(value: Int) {
            currentValue = max(currentValue - value, 0)
        }

        fun hasFinished() = calcProgress() == 1f
        fun hasType(type: Type) = this.type == type
        fun getProgress() = calcProgress()
        fun startTime() = startTime
        fun updateTime() = updateTime
        private fun calcProgress() = currentValue / maxValue.toFloat()
        enum class Type { NONE, BUILDING, IDLE, UPGRADE, REPAIR }
    }
}