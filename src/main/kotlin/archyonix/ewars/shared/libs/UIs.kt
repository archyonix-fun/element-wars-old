package archyonix.ewars.shared.libs

enum class UIs(
    val title: String
) {
    BUILDER_HUT("§f\uE032\uE037"),
    CRAFTSMAN_HOUSE("§f\uE032\uE038"),
    MILL_SHOP("§f\uE032\uE039"),
    ;

    companion object {
        const val BUILD_MENU = "§f\uE032\uE033"
        const val WALL_MENU = "§f\uE032\uE034"
        const val GATE_MENU = "§f\uE032\uE035"
        const val CAMP_FIRE_MENU = "§f\uE032\uE036"
        const val WOOD_TOWER_MENU = "§f\uE032\uE040"
    }
}