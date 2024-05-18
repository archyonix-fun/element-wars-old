package archyonix.ewars.features.action

import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.reflections.Reflections

@SuppressWarnings("all")
class ActionManager {
    private val executorClass: MutableMap<String, Class<out Action.Executor>> = HashMap()

    init {
        val reflections = Reflections("archyonix.ewars.features.action.executors")
        reflections.getTypesAnnotatedWith(Action.RegisteredExecutor::class.java)
            .forEach {
                if (it.superclass == Action.Executor::class.java) {
                    val name = it.getAnnotation(Action.RegisteredExecutor::class.java).name
                    @Suppress("UNCHECKED_CAST")
                    executorClass[name] = it as Class<out Action.Executor>
                }
            }
    }

    fun readActions(config: ConfigurationSection?): Set<Action> {
        val actionSet: MutableSet<Action> = HashSet()
        if (config == null) return actionSet
        for (actionId in config.getKeys(false)) {
            val actionData = config.getConfigurationSection(actionId)!!

            val triggers = actionData.getStringList("trigger").toSet()
            val executorName = actionData.getString("executor") ?: continue
            val properties = readProperties(actionData)

            val executor = createExecutor(executorName, properties) ?: continue
            actionSet.add(Action(actionId, triggers, executor))
        }
        return actionSet
    }

    private fun createExecutor(name: String, properties: Map<String, Any> = HashMap()): Action.Executor? {
        val exClass = executorClass[name] ?: return null
        return exClass.getDeclaredConstructor(Map::class.java)
            .newInstance(properties)
    }

    private fun readProperties(config: ConfigurationSection): Map<String, Any> {
        val properties: MutableMap<String, Any> = HashMap()
        val section = config.getConfigurationSection("properties") ?: return properties
        for (id in section.getKeys(false)) {
            properties[id] = section.get(id)!!
        }
        return properties
    }
}