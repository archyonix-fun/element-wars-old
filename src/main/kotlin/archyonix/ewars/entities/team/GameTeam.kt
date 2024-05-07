package archyonix.ewars.entities.team

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.team.data.ResourceStorage
import archyonix.ewars.game
import archyonix.ewars.game.bars.TeamBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
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
    val prefix = Component.text("⚑").color(this.color)
    val minecraftTeam = createMinecraftTeam()
    var resources: ResourceStorage = ResourceStorage()
    var infoBar: TeamBar = TeamBar(resources)
    var spawn = Pos(.0, .0, .0)

    fun getPlayers() = game.getPlayers().filter {
        try {
            val team = it.gameTeam()
            team.id == id
        } catch (_: Error) {
            false
        }
    }

    fun sendMessage(message: String) = sendMessage(Component.text(message))
    fun sendMessage(message: Component) {
        getPlayers().forEach {
            it.sendMessage(
                Component.join(
                    JoinConfiguration.spaces(),
                    prefix, message
                )
            )
        }
    }

    private fun createMinecraftTeam(): Team {
        val manager = MinecraftServer.getTeamManager()
        var team = manager.getTeam(id)

        if (team == null) {
            team = manager.createTeam(id)
            team.updatePrefix(prefix)
        }
        return team!!
    }
}