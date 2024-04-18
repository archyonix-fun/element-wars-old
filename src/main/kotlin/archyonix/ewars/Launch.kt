package archyonix.ewars

import archyonix.ewars.shared.args.GameArgs
import net.minestom.server.MinecraftServer

public var gameArgs: GameArgs = GameArgs(emptyArray())
fun main(args: Array<String>) {
    gameArgs = GameArgs(args)

    val server = MinecraftServer.init()
    server.start("0.0.0.0", 25565)
}