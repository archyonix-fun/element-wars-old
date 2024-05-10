package archyonix.ewars.entities.structure.controllers

import archyonix.ewars.entities.region.Region2D
import archyonix.ewars.entities.structure.Structure
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

fun Int.normalizeDirection(): Int = this % 8
private val wallDirections = arrayOf(arrayOf(0, 1, 2), arrayOf(7, -1, 3), arrayOf(6, 5, 4))

class StructureTower(name: String, player: Player) : Structure(name, player) {
    private val walls = Array(8) { Wall.NONE }
    private val cell = config.area / 3.0

    override fun registerStructure(): Boolean = structureManager.addStructure(this)

    private fun findDirection(pos: Point): Int? {
        var x = -1
        var z = -1

        for (index in 1..3) {
            val step = (index * cell)
            if (x < 0 && pos.blockX() < region.startX + step) x = index
            if (z < 0 && pos.blockZ() < region.startZ + step) z = index
            if (x != -1 && z != -1) break
        }

        if(x < 0 || z < 0) return null
        val result = wallDirections[z - 1][x - 1]
        return if (result >= 0) result else null
    }

    private fun getWallSchema(type: WallType): AppSchema {
        val schemaType = type.name.lowercase()
        val schemaPath = variant.data!!.getString("wallSchema-$schemaType")
        return AppSchema("config/structures/${name}/$schemaPath")
    }

    private fun findNeighbor(direction: Int): StructureTower? {
        val area = config.area
        val x = when (direction.normalizeDirection()) {
            0, 6, 7 -> position.blockX() - area
            1, 5 -> position.blockX()
            2, 3, 4 -> position.blockX() + area
            else -> throw RuntimeException("Unknown direction range 0..7")
        }
        val z = when (direction.normalizeDirection()) {
            0, 1, 2 -> position.blockZ() - area
            3, 7 -> position.blockZ()
            4, 5, 6 -> position.blockZ() + area
            else -> throw RuntimeException("Unknown direction range 0..7")
        }
        val neighbor =
            structureManager.findStructure<StructureTower>(Pos(x.toDouble(), position.y, z.toDouble())) ?: return null
        return if (neighbor.team != team) null else neighbor
    }

    private fun calcWallRegion(direction: Int): Region2D {
        val offsetX = when (direction) {
            0, 6, 7 -> -cell
            2, 3, 4 -> cell
            else -> 0
        }.toDouble()
        val offsetZ = when (direction) {
            0, 1, 2 -> -cell
            4, 5, 6 -> cell
            else -> 0
        }.toDouble()
        val pos = position.add(offsetX, .0, offsetZ).withGrid(cell.toInt())
        return Region2D(pos, floor(cell / 2.0).toInt(), true)
    }

    fun buildWall(pos: Point) {
        buildWall(findDirection(pos) ?: return)
    }

    private fun buildWall(direction: Int) {
        if (hasBuilding()) return
        if (walls[direction] != Wall.NONE) return
        val neighbor = findNeighbor(direction) ?: return
        if (neighbor.hasBuilding()) return

        val region = calcWallRegion(direction)

        val wallType = if (direction % 2 == 0) WallType.SLOPE else WallType.STRAIGHT
        val schema = getWallSchema(wallType)

        val structureDirection = when (direction) {
            0, 5, 4 -> Direction.EAST
            1 -> Direction.WEST
            3 -> Direction.NORTH
            else -> Direction.SOUTH
        }
        val buildPoint = Pos(region.startX.toDouble(), position.y, region.startZ.toDouble())
        try {
            schema.build(game.instance, structureDirection, buildPoint, team, useEffects = true)
            walls[direction] = Wall.WALL
            neighbor.buildWall((direction + 4).normalizeDirection())
        } catch (_: Exception) {
            buildWall(direction)
        }

    }

    fun showWallSketch(player: Player, pos: Point? = null) {
        if (hasBuilding()) return
        val zeroPosition = pos ?: player.position.withGrid(4)
        val direction = findDirection(zeroPosition) ?: return

        if (walls[direction] != Wall.NONE) return
        val neighbor = findNeighbor(direction) ?: return
        if (neighbor.hasBuilding()) return

        showRegion(calcWallRegion(direction), player)
        showRegion(neighbor.calcWallRegion((direction + 4).normalizeDirection()), player)
    }
}