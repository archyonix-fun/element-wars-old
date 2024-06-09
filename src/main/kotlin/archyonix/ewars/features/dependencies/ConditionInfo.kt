package archyonix.ewars.features.dependencies

import net.minestom.server.entity.Player

class ConditionInfo {
    private val conditions: MutableSet<Condition> = HashSet()
    private val playerConditions: MutableMap<Player, MutableSet<Condition>> = HashMap()

    fun addCondition(player: Player, condition: Condition) {
        val map: MutableSet<Condition> = playerConditions[player] ?: HashSet()
        map.add(condition)
        playerConditions[player] = map
    }

    fun addCondition(condition: Condition) {
        conditions.add(condition)
    }

    fun removeCondition(player: Player, condition: Condition) {
        val map: MutableSet<Condition> = playerConditions[player] ?: HashSet()
        map.remove(condition)
        playerConditions[player] = map
    }

    fun removeCondition(condition: Condition) {
        conditions.remove(condition)
    }

    fun checkCondition(condition: Condition): Boolean {
        return conditions.contains(condition)
    }

    fun checkCondition(player: Player, condition: Condition): Boolean {
        if(conditions.contains(condition)) return true
        val set = playerConditions[player] ?: return false
        return set.contains(condition)
    }

    fun buildMap() {

    }

    fun buildMapPlayer(player: Player) {

    }
}