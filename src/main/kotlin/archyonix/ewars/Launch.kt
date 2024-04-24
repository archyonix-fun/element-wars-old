package archyonix.ewars

import archyonix.ewars.entities.item.action.ActionManager
import archyonix.ewars.entities.structure.StructureData
import archyonix.ewars.game.Game
import archyonix.ewars.game.events.InitGameEvent
import archyonix.ewars.game.handler.ServerEventHandler
import archyonix.ewars.shared.args.GameArgs
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos

var events: ServerEventHandler? = null
var gameArgs: GameArgs? = null
var game: Game? = null
var actionManager = ActionManager()

fun main(args: Array<String>) {
    val server = MinecraftServer.init()

    gameArgs = GameArgs(args)
    game = Game()

    events = ServerEventHandler()
    events!!.call(InitGameEvent(game!!))
    MinecraftServer.getGlobalEventHandler().addChild(events!!)

    server.start("0.0.0.0", 25565)
}