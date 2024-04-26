package archyonix.ewars.entities.region

class RegionManager {
    private val regions: MutableSet<Region> = HashSet()

    fun addRegion(region: Region) = regions.add(region)
    fun remRegion(region: Region) = regions.remove(region)
    fun validateRegion(region: Region): Boolean {
        for(reg in regions) {
            if (reg.contains(region)) return false
        }
        return true
    }
}