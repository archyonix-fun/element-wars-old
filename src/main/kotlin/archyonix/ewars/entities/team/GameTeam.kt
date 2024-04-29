package archyonix.ewars.entities.team

import archyonix.ewars.entities.player._getTeam
import archyonix.ewars.game
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.scoreboard.Team

class GameTeam(
    val id: String,
    val displayName: String,
    val bannerColorFirst: String = "white",
    val bannerColorSecond: String = "white",
    color: String
) {
    val color = TextColor.fromHexString(color)
    val maxPlayers = game.map.TEAM_SIZE
    val minecraftTeam = createMinecraftTeam()
    var spawn = Pos(.0, .0, .0)

    fun getPlayers() = game.getPlayers().filter {
        try {
            val team = it._getTeam()
            team.id == id
        } catch (_: Error) {
            false
        }
    }

    private fun createMinecraftTeam(): Team {
        val manager = MinecraftServer.getTeamManager()
        var team = manager.getTeam(id)

        if (team == null) {
            team = manager.createTeam(id)
            team.updatePrefix(Component.text("⚑ ").color(color))
        }
        return team!!
    }
}