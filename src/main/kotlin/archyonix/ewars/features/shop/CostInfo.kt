package archyonix.ewars.features.shop

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.resource.Resource
import archyonix.ewars.entities.team.GameTeam
import net.minestom.server.entity.Player
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import java.util.*

class CostInfo(
    val resources: Map<Resource, Int> = EnumMap(Resource::class.java)
) {
    companion object {
        fun import(section: ConfigurationSection?): CostInfo? {
            if (section == null) return null
            val resources: MutableMap<Resource, Int> = EnumMap(Resource::class.java)
            for (key in section.getKeys(false)) {
                val resource = Resource.values().find { it.name == key } ?: continue
                resources[resource] = section.getInt(key)
            }
            return CostInfo(resources)
        }
    }

    fun isEmpty() = resources.isEmpty()

    fun buildLore(): List<String> {
        val lore: MutableList<String> = ArrayList()
        for (resource in resources.keys)
            lore.add("§f${resource.getIcon().content()}<text> - ${resources[resource]}")
        return lore
    }

    fun pay(player: Player): Boolean {
        if (!check(player.gameTeam())) return false

        val resources = player.gameTeam().resources
        for (resource in this.resources.keys) {
            when (resource) {
                Resource.NATURAL_CRYSTAL -> resources.NATURAL_CRYSTAL -= this.resources[resource]!!
                Resource.FLAME_CRYSTAL -> resources.FLAME_CRYSTAL -= this.resources[resource]!!
                Resource.EARTH_CRYSTAL -> resources.EARTH_CRYSTAL -= this.resources[resource]!!
                Resource.WATER_CRYSTAL -> resources.WATER_CRYSTAL -= this.resources[resource]!!
            }
        }
        return true
    }

    fun check(gameTeam: GameTeam): Boolean {
        val resources = gameTeam.resources
        for (resource in this.resources.keys) {
            when (resource) {
                Resource.NATURAL_CRYSTAL -> if (resources.NATURAL_CRYSTAL < this.resources[resource]!!) return false
                Resource.FLAME_CRYSTAL -> if (resources.FLAME_CRYSTAL < this.resources[resource]!!) return false
                Resource.EARTH_CRYSTAL -> if (resources.EARTH_CRYSTAL < this.resources[resource]!!) return false
                Resource.WATER_CRYSTAL -> if (resources.WATER_CRYSTAL < this.resources[resource]!!) return false
            }
        }
        return true
    }
}