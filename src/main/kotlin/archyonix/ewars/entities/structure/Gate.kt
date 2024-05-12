package archyonix.ewars.entities.structure

import archyonix.ewars.entities.player.gameTeam
import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.region.Zone
import archyonix.ewars.entities.structure.controllers.StructureTower
import archyonix.ewars.entities.structure.enums.Wall
import archyonix.ewars.game
import net.minestom.server.instance.block.Block

private val gateList: MutableSet<Gate> = HashSet()
fun updateAllGates() = gateList.forEach { it.checkOpened() }
fun findGateByParents(parent1: Structure, parent2: Structure) = gateList
    .filter { it.containsParent(parent1) }
    .find { it.containsParent(parent2) }

class Gate(
    private val parents: Map<StructureTower, Int>,
    private val region: Region2D,
) {
    private val parent1 = parents.keys.first()
    private val parent2 = parents.keys.last()

    private val team = parents.keys.first().team
    private val height = parents.keys.first().config.height
    private val minY = parents.keys.first().position.blockY()
    private val direction = parents[parents.keys.first()]!!
    private val isVertical = when (direction) {
        1, 5 -> true
        else -> false
    }
    private val firstZone = calcFirstZone()
    private val secondZone = calcSecondZone()
    private val firstPattern = getPattern(parent1)
    private val secondPattern = getPattern(parent2)


    private var isOpened: Boolean = false

    init {
        gateList.add(this)
        for (tower in parents.keys) {
            val direction = parents[tower]!!
            tower.walls[direction] = Wall.GATE
        }
        game.schedule.scheduleNextTick {
            firstZone.fill(firstPattern)
            secondZone.fill(secondPattern)
        }
    }

    private fun getPattern(parent: StructureTower): Block {
        val direction = parent.calcStructureDirection(direction)
        val patternId = parent.variant.data?.getInt("gatePattern") ?: 0
        return parent.getGateSchema().getBlockFromPalette(patternId, direction)
    }

    private fun calcFirstZone(): Zone {
        val minX = region.startX + (region.width / 2)
        val maxX = minX + if (isVertical) 1 else 0
        val minZ = region.startZ + (region.length / 2)
        val maxZ = minZ + if (isVertical) 0 else 1
        val maxY = minY + 2
        return Zone(minX, minY, minZ, maxX, maxY, maxZ)
    }

    private fun calcSecondZone(): Zone {
        val maxX = region.endX - (region.width / 2)
        val minX = maxX - if (isVertical) 1 else 0
        val maxZ = region.endZ - (region.length / 2)
        val minZ = maxZ - if (isVertical) 0 else 1
        val maxY = minY + 2
        return Zone(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun containsParent(structure: Structure): Boolean {
        return parents.keys.find { it.uuid == structure.uuid } != null
    }

    fun deconstruct() {
        gateList.remove(this)
        for (tower in parents.keys) {
            tower.walls[parents[tower]!!] = Wall.NONE
        }
        clear()
    }

    private fun clear() {
        Zone(
            region.startX, minY, region.startZ,
            region.endX, minY + height, region.endZ
        ).fill(Block.AIR)
    }

    fun checkOpened() {
        for (player in game.getPlayers()) {
            if (player.gameTeam() != team) continue
            if (!region.contains(player.position)) continue
            open()
            return
        }
        close()
    }

    private fun open() {
        if (isOpened) return
        firstZone.fill(Block.AIR)
        secondZone.fill(Block.AIR)
        isOpened = true
    }

    private fun close() {
        if (!isOpened) return
        firstZone.fill(firstPattern)
        secondZone.fill(secondPattern)
        isOpened = false
    }
}