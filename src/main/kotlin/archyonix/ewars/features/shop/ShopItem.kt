package archyonix.ewars.features.shop

import archyonix.ewars.entities.item.GameItem
import archyonix.ewars.entities.structure.Dependence
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.shared.libs.LoreUtils

class ShopItem(
    val gameItem: GameItem,
    val amount: Int,
    val dependencies: List<Dependence>,
    val costInfo: CostInfo
) {
    fun buildLore(team: GameTeam): List<String> {
        val lore = mutableListOf<String>()
        lore.addAll(LoreUtils.buildCostLore(costInfo))
        lore.addAll(LoreUtils.buildDependenciesLore(dependencies, team))
        return lore
    }

    fun checkDependencies(team: GameTeam): Boolean {
        for (dep in dependencies) {
            if (!dep.check(team)) return false
        }
        return true
    }
}