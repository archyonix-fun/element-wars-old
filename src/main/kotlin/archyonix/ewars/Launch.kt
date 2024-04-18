package archyonix.ewars

import net.minestom.server.MinecraftServer

fun main() {
    val server = MinecraftServer.init()
    server.start("0.0.0.0", 25565)
}