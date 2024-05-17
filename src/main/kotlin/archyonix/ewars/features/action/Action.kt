package archyonix.ewars.features.action

import net.minestom.server.entity.Player

class Action(
    val trigger: String,
    private val executor: Executor
) {
    fun execute(player: Player) = executor.run(player)

    abstract class Executor(
        val properties: Map<String, Any>
    ) {
        abstract fun run(player: Player): Boolean
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class RegisteredExecutor(
        val name: String
    )
}