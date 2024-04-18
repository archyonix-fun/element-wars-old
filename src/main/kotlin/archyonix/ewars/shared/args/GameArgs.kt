package archyonix.ewars.shared.args

class GameArgs(private val args: Array<String>) {
    init {
        args.size != 3 && throw Exception("Uncorrected game args. Fix to (mapName, callbackHost, playerList)")
    }

    val MAP_NAME = args[0]
    val CALLBACK_HOST = args[1]
    val PLAYER_LIST = args[2].split(",").toTypedArray()
}