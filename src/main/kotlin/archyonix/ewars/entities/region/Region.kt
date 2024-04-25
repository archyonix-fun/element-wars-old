package archyonix.ewars.entities.region

class Region(
    val startX: Int,
    val startZ: Int,
    val endX: Int,
    val endZ: Int,
) {
    fun contains(region: Region): Boolean {
        return ((region.startX in startX..endX) || (region.endX in startX..endX))
                && ((region.startZ in startZ..endX) || (region.endZ in endZ..endZ))
    }
}