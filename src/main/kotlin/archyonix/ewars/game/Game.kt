package archyonix.ewars.game

import archyonix.ewars.entities.structure.StructureManager
import archyonix.ewars.entities.map.GameMap
import archyonix.ewars.entities.player.PlayerType
import archyonix.ewars.entities.player.getRole
import archyonix.ewars.entities.resource.ResourceManager
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.events
import archyonix.ewars.features.action.ActionManager
import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.game.enums.GameStage
import archyonix.ewars.game.events.ChangeGameStageEvent
import archyonix.ewars.game.handler.GameEventHandler
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

    lateinit var classLibrary: ClassLibrary
    lateinit var actionManager: ActionManager
    lateinit var resourceManager: ResourceManager
    lateinit var handler: GameEventHandler
    lateinit var structureManager: StructureManager

    val isDebug: Boolean = gameArgs.IS_DEBUG.toBooleanStrictOrNull() ?: false

    fun init(): Game {
        this.classLibrary = ClassLibrary()
        this.actionManager = ActionManager()
        this.resourceManager = ResourceManager()
        this.handler = GameEventHandler()
        this.structureManager = StructureManager()

        return this
    }

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