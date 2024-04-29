package archyonix.ewars.entities.item.action

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Executor(
    val name: String
)
