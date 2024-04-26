package archyonix.ewars

import archyonix.ewars.entities.item.action.ActionManager
import archyonix.ewars.entities.structure.StructureManager
import archyonix.ewars.game.Game
import archyonix.ewars.game.events.InitGameEvent
import archyonix.ewars.game.handler.ServerEventHandler
import archyonix.ewars.shared.args.GameArgs
import net.minestom.server.MinecraftServer

lateinit var events: ServerEventHandler
lateinit var gameArgs: GameArgs
lateinit var game: Game
val actionManager = ActionManager()
val structureManager = StructureManager()

fun main(args: Array<String>) {
    val server = MinecraftServer.init()

    gameArgs = GameArgs(args)
    game = Game()

    events = ServerEventHandler()
    events.call(InitGameEvent(game))
    MinecraftServer.getGlobalEventHandler().addChild(events)

    server.start("0.0.0.0", 25565)
}