package archyonix.ewars.entities.region

class Region(
    val startX: Int,
    val startZ: Int,
    val endX: Int,
    val endZ: Int,
) {
    fun contains(region: Region): Boolean {
        if(region.startX > endX) return true
        if(region.endX < startX) return true

        if(region.startZ > endZ) return true
        return region.endZ < startZ
    }
}