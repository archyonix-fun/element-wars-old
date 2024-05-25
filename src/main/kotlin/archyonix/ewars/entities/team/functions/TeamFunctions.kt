package archyonix.ewars.entities.team.functions

import archyonix.ewars.entities.player.setGameTeam
import archyonix.ewars.entities.player.backToSpawn
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.game
import net.minestom.server.coordinate.Pos
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File
import java.util.*

private fun loadTeams(count: Int): Set<GameTeam> {
    val teamSet: MutableSet<GameTeam> = HashSet()
    val configFile = File("config/teams.yaml")
    val config = YamlConfiguration.loadConfiguration(configFile)
    for (id in config.getKeys(false)) {
        val name = config.getString("$id.name")!!
        val color = config.getString("$id.color")!!
        val bannerColorFirst = config.getString("$id.bannerColorFirst")!!
        val bannerColorSecond = config.getString("$id.bannerColorSecond")!!
        val teamData = GameTeam(id, name, bannerColorFirst, bannerColorSecond, color)
        teamSet.add(teamData)
        if (teamSet.size >= count) break
    }
    if (teamSet.size < count) {
        throw Error("The number of configured teams does not match the required value: ${teamSet.size} < $count")
    }
    return teamSet
}

fun useGameTeams(): Map<String, GameTeam> {
    val gameTeams: MutableMap<String, GameTeam> = HashMap()
    val spawnList: List<Pos> = game.map.SPAWN_LIST
    for ((n, team) in loadTeams(game.map.TEAM_COUNT).withIndex()) {
        team.spawn = spawnList[n]
        gameTeams[team.id] = team
    }
    return gameTeams
}

private fun pickRandomFreeTeam(): GameTeam {
    val freeTeams: MutableList<GameTeam> = ArrayList()
    for (team in game.teams!!.values) {
        if (team.getPlayers().size < team.maxPlayers) freeTeams.add(team)
    }
    if(freeTeams.isEmpty()) throw Error("All teams are full")

    val random = Random()
    return freeTeams[random.nextInt(freeTeams.size)]
}

fun distributeTeams() {
    for (player in game.getPlayers()) {
        val team = pickRandomFreeTeam()
        player.setGameTeam(team)
        player.backToSpawn()
    }
}

fun findTeamById(id: String) = game.teams!![id]