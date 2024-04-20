package archyonix.ewars.shared.lib

import archyonix.ewars.game.Game
import archyonix.ewars.game.events.InitGameEvent
import archyonix.ewars.game.handler.ServerEventHandler
import archyonix.ewars.shared.lib.shared.args.GameArgs
import net.minestom.server.MinecraftServer

var events: ServerEventHandler? = null
var gameArgs: GameArgs? = null
var game: Game? = null

fun main(args: Array<String>) {
    val server = MinecraftServer.init()

    gameArgs = GameArgs(args)
    game = Game()

    events = ServerEventHandler()
    events!!.call(InitGameEvent(game!!))
    MinecraftServer.getGlobalEventHandler().addChild(events!!)

    server.start("0.0.0.0", 25565)
}