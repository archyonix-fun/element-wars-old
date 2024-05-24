package archyonix.ewars.features.action

import org.antlr.v4.runtime.atn.SemanticContext.AND
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class Action(
    val id: String,
    val trigger: Trigger,
    private val executor: Executor
) {
    fun execute(caller: Any) = executor.run(caller)

    abstract class Executor(
        val properties: Map<String, Any>
    ) {
        abstract fun run(caller: Any): Boolean
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class RegisteredExecutor(
        val name: String
    )

    // Child classes
    class Trigger(
        config: Config
    ) {
        private val condition: Condition = config.condition
        fun contains(values: Map<String, Boolean>): Boolean {
            return condition.contains(values)
        }

        // Child classes
        class Config(
            config: ConfigurationSection?
        ) {
            val condition: Condition = importConditions(config)

            private fun importConditions(config: ConfigurationSection?): Condition {
                if(config == null) return Condition.Group(emptySet(), Condition.Group.Operator.AND)
                val operator = Condition.Group.Operator.values()
                    .find { it.name == config.name.uppercase() } ?: Condition.Group.Operator.AND
                val conditions: MutableSet<Condition> = HashSet()
                for (sectionId in config.getKeys(false)) {
                    if (config.isConfigurationSection(sectionId)) {
                        conditions.add(importConditions(config.getConfigurationSection(sectionId)!!))
                    } else conditions.add(Condition.Impl(sectionId, config.getBoolean(config.name, false)))
                }
                return Condition.Group(conditions, operator)
            }
        }

        interface Condition {
            fun contains(values: Map<String, Boolean>): Boolean

            class Impl(
                val name: String,
                private val invert: Boolean
            ) : Condition {
                override fun contains(values: Map<String, Boolean>): Boolean {
                    return values[name] == !invert
                }
            }

            class Group(
                private val conditions: Set<Condition>,
                private val operator: Operator = Operator.AND
            ) : Condition {
                override fun contains(values: Map<String, Boolean>): Boolean {
                    for (condition in conditions) {
                        when (operator) {
                            Operator.AND -> if (!condition.contains(values)) return false
                            Operator.OR -> if (condition.contains(values)) return true
                        }
                    }
                    return true
                }

                enum class Operator { AND, OR }
            }
        }
    }
}