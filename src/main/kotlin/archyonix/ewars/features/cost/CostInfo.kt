package archyonix.ewars.features.cost

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.resource.Resource
import net.minestom.server.entity.Player
import java.util.*

class CostInfo(
    private val resources: Map<Resource, Int> = EnumMap(Resource::class.java)
) {
    fun isEmpty() = resources.isEmpty()

    fun buildLore(): List<String> {
        val lore: MutableList<String> = ArrayList()
        for (resource in resources.keys)
            lore.add("§f${resource.getIcon().content()}<text> - ${resources[resource]}")
        return lore
    }

    fun pay(player: Player): Boolean {
        val resources = player.gameTeam().resources

        for (resource in this.resources.keys) {
            when(resource) {
                Resource.NATURAL_CRYSTAL -> if(resources.NATURAL_CRYSTAL < this.resources[resource]!!) return false
                Resource.FLAME_CRYSTAL -> if(resources.FLAME_CRYSTAL < this.resources[resource]!!) return false
                Resource.EARTH_CRYSTAL -> if(resources.EARTH_CRYSTAL < this.resources[resource]!!) return false
                Resource.WATER_CRYSTAL -> if(resources.WATER_CRYSTAL < this.resources[resource]!!) return false
            }
        }

        for (resource in this.resources.keys) {
            when(resource) {
                Resource.NATURAL_CRYSTAL -> resources.NATURAL_CRYSTAL -= this.resources[resource]!!
                Resource.FLAME_CRYSTAL -> resources.FLAME_CRYSTAL -= this.resources[resource]!!
                Resource.EARTH_CRYSTAL -> resources.EARTH_CRYSTAL -= this.resources[resource]!!
                Resource.WATER_CRYSTAL -> resources.WATER_CRYSTAL -= this.resources[resource]!!
            }
        }

        return true
    }
}