package archyonix.ewars.features.action

import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class ActionManager {
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
        val exConstructor = game.classLibrary
            .findConstructor<Action.Executor>(name, ClassLibrary.Type.EXECUTOR) ?: return null
        
        return exConstructor.newInstance(properties)
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