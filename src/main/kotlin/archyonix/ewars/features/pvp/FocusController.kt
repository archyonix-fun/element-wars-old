package archyonix.ewars.features.pvp

import net.minestom.server.entity.Player
import java.util.*

class FocusController {
    private val FOCUS_DELAY = 500L

    private val focusMap: MutableMap<Player, Focus> = HashMap()
    private val focusCD: MutableMap<Player, Long> = HashMap()

    fun useFocus(player: Player, focus: Focus) {
        val current = Date().time
        val cd = focusCD[player] ?: 0L

        if (focusMap.containsKey(player)) return
        if (current - cd < FOCUS_DELAY) return

        focusMap[player] = focus
    }
    fun getFocus(player: Player) = focusMap[player] ?: Focus.NONE
    fun removeFocus(player: Player) = focusMap.remove(player)
    enum class Focus { NONE, BLUE, RED }
}