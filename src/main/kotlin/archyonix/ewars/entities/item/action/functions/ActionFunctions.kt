package archyonix.ewars.entities.item.action.functions

import archyonix.ewars.entities.item.action.Action
import archyonix.ewars.entities.item.action.Executor
import archyonix.ewars.entities.item.action.IExecutor
import archyonix.ewars.features.shop.CostInfo
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import org.reflections.Reflections

val executors = loadExecutors()
private fun loadExecutors(): Map<String, IExecutor> {
    val reflections = Reflections("archyonix.ewars.entities.item.action.executors")
    val executorClassSet: Set<Class<out IExecutor>> =
        reflections.getTypesAnnotatedWith(Executor::class.java)
            .map { it as Class<out IExecutor> }.toSet()

    val map: MutableMap<String, IExecutor> = HashMap()
    for (executorClass in executorClassSet) {
        val name = executorClass.getAnnotation(Executor::class.java).name
        val executor = executorClass.getDeclaredConstructor().newInstance()
        map[name] = executor
    }
    return map
}

fun ConfigurationSection.loadActions(): Set<Action> {
    val actionSet: MutableSet<Action> = HashSet()
    val section = getConfigurationSection("actions") ?: return actionSet
    for (actionId in section.getKeys(false)) {
        val config = section.getConfigurationSection(actionId)!!
        actionSet.add(Action(actionId, config))
    }
    return actionSet
}
