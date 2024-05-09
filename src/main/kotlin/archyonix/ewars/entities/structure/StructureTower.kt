package archyonix.ewars.entities.structure

import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.enums.Wall
import archyonix.ewars.entities.structure.enums.WallType
import archyonix.ewars.entities.structure.functions.showRegion
import archyonix.ewars.entities.structure.functions.withGrid
import archyonix.ewars.game
import archyonix.ewars.shared.libs.AppSchema
import archyonix.ewars.structureManager
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.utils.Direction
import kotlin.math.floor

class StructureTower(
    val structure: Structure
) {
    private val walls = Array(8) { Wall.NONE }

    fun showEffectIfWallIsNone(player: Player, pos: Point? = null) {
        if(structure.hasBuilding()) return
        val zeroPosition = pos ?: player.position.withGrid(4)
        val direction = findDirection(zeroPosition)
        if (direction < 0) return

        if (walls[direction] != Wall.NONE) return
        val towerOther = findNeighbor(direction) ?: return
        if(towerOther.structure.hasBuilding()) return

        val firstRegion = wallRegion(direction)
        val secondRegion = towerOther.wallRegion(direction + 4)

        showRegion(firstRegion, player)
        showRegion(secondRegion, player)
    }

    fun buildWall(direction: Int) {
        if(structure.hasBuilding()) return
        if(direction < 0) return
        val dir = direction.normalizeDirection()

        if (walls[dir] != Wall.NONE) return
        val towerOther = findNeighbor(dir) ?: return
        if(towerOther.structure.hasBuilding()) return

        val region = wallRegion(dir)

        val wallType = if (dir % 2 == 0) WallType.SLOPE else WallType.STRAIGHT
        val schema = wallSchema(wallType)

        val structureDirection = when(dir) {
            0, 1, 4, 5 -> Direction.EAST
            else -> Direction.SOUTH
        }
        val buildPos = Pos(region.startX.toDouble(), structure.position.y, region.startZ.toDouble())
        schema.build(game.instance, structureDirection, buildPos, structure.team, useEffects = true)
        walls[dir] = Wall.WALL

        towerOther.buildWall(dir + 4)
    }

    private fun wallSchema(type: WallType): AppSchema {
        val schemaType = type.name.lowercase()
        val variant = structure.variant
        val schemaPath = variant.data!!.getString("wallSchema-$schemaType")
        return AppSchema("config/structures/${structure.name}/$schemaPath")
    }

    fun findDirection(pos: Point): Int {
        val cell = structure.config.area / 3
        var x = -1
        for (index in 1..3) {
            if (pos.blockX() < structure.region.startX + (index * cell)) {
                x = index
                break
            }
        }
        var z = -1
        for (index in 1..3) {
            if (pos.blockZ() < structure.region.startZ + (index * cell)) {
                z = index
                break
            }
        }
        return if (x == 1 && z == 1) 0
        else if (x == 2 && z == 1) 1
        else if (x == 3 && z == 1) 2
        else if (x == 3 && z == 2) 3
        else if (x == 3 && z == 3) 4
        else if (x == 2 && z == 3) 5
        else if (x == 1 && z == 3) 6
        else if (x == 1 && z == 2) 7
        else -1
    }

    private fun findNeighbor(direction: Int): StructureTower? {
        val area = structure.config.area
        val x = when (direction.normalizeDirection()) {
            0, 6, 7 -> structure.position.blockX() - area
            1, 5 -> structure.position.blockX()
            2, 3, 4 -> structure.position.blockX() + area
            else -> throw RuntimeException("Unknown direction range 0..7")
        }
        val z = when (direction.normalizeDirection()) {
            0, 1, 2 -> structure.position.blockZ() - area
            3, 7 -> structure.position.blockZ()
            4, 5, 6 -> structure.position.blockZ() + area
            else -> throw RuntimeException("Unknown direction range 0..7")
        }
        val otherTower = structureManager.findTower(
            Pos(x.toDouble(), structure.position.y, z.toDouble())
        )
        return if (otherTower == null) null
        else if (otherTower.structure.team != this.structure.team) null
        else otherTower
    }

    private fun wallRegion(direction: Int): Region2D {
        val cell = structure.config.area / 3.0
        val pos = structure.position

        val rgPos = when (direction.normalizeDirection()) {
            0 -> pos.add(-cell)
            1 -> pos.add(.0, .0, -cell)
            2 -> pos.add(cell, .0, -cell)
            3 -> pos.add(cell, .0, .0)
            4 -> pos.add(cell, .0, cell)
            5 -> pos.add(.0, .0, cell)
            6 -> pos.add(-cell, .0, cell)
            7 -> pos.add(-cell, .0, .0)
            else -> throw RuntimeException("Unknown direction range 0..7")
        }.withGrid(cell.toInt())

        return Region2D(rgPos, floor(cell / 2.0).toInt(), true)
    }
}

fun Structure.isTower(): Boolean = (name == "TOWER")
fun Int.normalizeDirection(): Int = this % 8