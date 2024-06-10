package archyonix.ewars.shared.libs

import archyonix.ewars.features.shop.CostInfo
import archyonix.ewars.features.dependencies.Dependence

class LoreUtils {
    companion object {
        fun buildCostLore(info: CostInfo): List<String> {
            val lore = mutableListOf<String>()
            if (!info.isEmpty()) {
                lore.add("")
                lore.add("<text>[<yellow>Ресурсы<text>]:")
                lore.addAll(info.buildLore())
            }
            return lore
        }

        fun buildDependenciesLore(dependencies: List<Dependence>): List<String> {
            val lore = mutableListOf<String>()
            if (dependencies.isNotEmpty()) {
                val lines = mutableListOf<String>()
                dependencies.forEach {
                    it.generateLore()
                        ?.let { line -> lines.add(line) }
                }
                if (lines.isNotEmpty()) {
                    lore.add("")
                    lore.add("<text>[<blue>Требования<text>]:")
                    lore.addAll(lines)
                }
            }
            return lore
        }
    }
}