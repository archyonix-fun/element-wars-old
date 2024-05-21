package archyonix.ewars.entities.structure.types

import net.minestom.server.coordinate.Vec
import net.minestom.server.utils.Direction

class StructureOffset {
    enum class Local2D(
        val vector: Vec,
    ) {
        FRONT_LEFT(Vec(1.0, 1.0)),
        FRONT(Vec(0.0, 1.0)),
        RIGHT_FRONT(Vec(-1.0, 1.0)),
        RIGHT(Vec(-1.0, 0.0)),
        BACK_RIGHT(Vec(-1.0, -1.0)),
        BACK(Vec(0.0, -1.0)),
        LEFT_BACK(Vec(1.0, -1.0)),
        LEFT(Vec(1.0, 0.0));

        fun reflect(): Local2D {
            val vector = vector.mul(-1.0)
            return values().find { it.vector.blockX() == vector.blockX() && it.vector.blockZ() == vector.blockZ() }!!
        }
    }

    enum class Global2D(
        val index: Int,
        val direction: Direction,
        val vector: Vec,
    ) {
        NORTH_WEST(0, Direction.WEST, Vec(-1.0, -1.0)),
        NORTH(1, Direction.WEST, Vec(0.0, -1.0)),
        EAST_NORTH(2, Direction.NORTH, Vec(1.0, -1.0)),
        EAST(3, Direction.NORTH, Vec(1.0, 0.0)),
        SOUTH_EAST(4, Direction.EAST, Vec(1.0, 1.0)),
        SOUTH(5, Direction.EAST, Vec(0.0, 1.0)),
        WEST_SOUTH(6, Direction.SOUTH, Vec(-1.0, 1.0)),
        WEST(7, Direction.SOUTH, Vec(-1.0, 0.0));

        fun reflect(): Global2D {
            val vector = vector.mul(-1.0)
            return values().find { it.vector.blockX() == vector.blockX() && it.vector.blockZ() == vector.blockZ() }!!
        }
    }
}