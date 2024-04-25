package archyonix.ewars.entities.structure

class Region(
    val startX: Int,
    val startZ: Int,
    val endX: Int,
    val endZ: Int,
) {
    fun contains(region: Region): Boolean {
        return if (
            (region.startX in startX..endX)
            ||
            (region.endX in startX..endX)
        ) true
        else ((region.startZ in startZ..endX)
                ||
                (region.endZ in endZ..endZ))
    }
}