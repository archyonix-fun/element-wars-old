package archyonix.ewars.game

import archyonix.ewars.entities.map.GameMap
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.getRole
import archyonix.ewars.entities.resource.ResourceManager
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.events
import archyonix.ewars.features.action.ActionManager
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.game.events.ChangeGameStageEvent
import archyonix.ewars.gameArgs
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File
import kotlin.properties.Delegates

class Game {
    val map = GameMap(gameArgs.MAP_NAME)
    val instance = GameInstance(map)
    val schedule = instance.scheduler()
    var teams: Map<String, GameTeam>? = null
    val messages: Map<String, String> = loadMessages()
    var startTime by Delegates.notNull<Long>()

    val actionManager = ActionManager()
    val resourceManager = ResourceManager()

    val isDebug: Boolean = gameArgs.IS_DEBUG.toBooleanStrictOrNull() ?: false

    var stage = GameStage.EXPECTATION
        set(value) {
            field = value
            events.call(ChangeGameStageEvent(value))
        }
    val maxPlayers = map.TEAM_COUNT * map.TEAM_SIZE

    fun getPlayers() = instance.players.filter { it.getRole() == PlayerType.PLAYER }
    private fun loadMessages(): Map<String, String> {
        val messages = YamlConfiguration.loadConfiguration(File("config/messages.yaml"))
        return messages.getKeys(false).associateWith { messages.getString(it, "")!! }
    }
}