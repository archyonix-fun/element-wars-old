package archyonix.ewars.shared.args

class GameArgs(private val args: Array<String>) {
    init {
        args.size != 4 && throw Exception("Uncorrected game args. Fix to (mapName, callbackHost, isDebug, playerList)")
    }

    val MAP_NAME = args[0]
    val CALLBACK_HOST = args[1]
    val IS_DEBUG = args[2]
    val PLAYER_LIST = args[3].split(",").toTypedArray()
}