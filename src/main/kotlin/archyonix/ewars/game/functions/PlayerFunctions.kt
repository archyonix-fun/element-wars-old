package archyonix.ewars.game.functions

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player

fun configurePlayer(player: Player) {
    player.setGameMode(GameMode.ADVENTURE)
}
fun configureSpectator(player: Player) {
    player.setGameMode(GameMode.SPECTATOR)
    player.isInvisible = true
}

fun findPlayerByUsername(username: String) =
    MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(username)