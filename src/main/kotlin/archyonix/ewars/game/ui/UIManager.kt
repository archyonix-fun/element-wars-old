package archyonix.ewars.game.ui

import net.minestom.server.entity.Player
import org.reflections.Reflections

class UIManager {
    private val registeredUIClasses: MutableMap<String, Class<out GameUI>> = HashMap()

    init {
        val reflections = Reflections("archyonix.ewars.game.ui.registered")
        reflections.getTypesAnnotatedWith(GameUI.RegisteredUI::class.java)
            .forEach {
                if (it.superclass == GameUI::class.java) {
                    val name = it.getAnnotation(GameUI.RegisteredUI::class.java).name
                    @Suppress("UNCHECKED_CAST")
                    registeredUIClasses[name] = it as Class<out GameUI>
                }
            }
    }

    fun createUI(player: Player, name: String): GameUI? {
        val uiClass = registeredUIClasses[name] ?: return null
        return try {
            uiClass
                .getDeclaredConstructor(Player::class.java)
                .newInstance(player)
        } catch (e: Exception) {
            null
        }
    }
}