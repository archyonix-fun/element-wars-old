package archyonix.ewars.entities.team

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.resource.ResourceStorage
import archyonix.ewars.features.dependencies.ConditionInfo
import archyonix.ewars.game
import archyonix.ewars.game.bars.TeamBar
import archyonix.ewars.shared.libs.withColors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.scoreboard.Team
import java.util.UUID

class GameTeam(
    val id: String,
    val name: String,
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
    val conditions = ConditionInfo()

    fun getPlayers() = game.getPlayers().filter {
        try {
            val team = it.gameTeam()
            team.id == id
        } catch (_: Error) {
            false
        }
    }

    fun showMessage(message: String) = showMessage(message.withColors())
    fun showMessage(message: Component) {
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
        val id = "$id:${UUID.randomUUID()}"
        val manager = MinecraftServer.getTeamManager()
        var team = manager.getTeam(id)

        if (team == null) {
            team = manager.createTeam(id)
            team.updatePrefix(prefix)
        }
        return team!!
    }
}