package archyonix.ewars.entities.message

import archyonix.ewars.game

class Message {
    companion object {
        val NOT_ENOUGH_MONEY = game.messages["NOT_ENOUGH_MONEY"] ?: ""
        val BUILDING_LIMIT_EXCEEDED = game.messages["BUILDING_LIMIT_EXCEEDED"] ?: ""
        val PLAYER_BOUGHT_THE_BUILDING = game.messages["PLAYER_BOUGHT_THE_BUILDING"] ?: ""
        val START_BUILDING = game.messages["START_BUILDING"] ?: ""
        val COMPLETE_BUILDING = game.messages["COMPLETE_BUILDING"] ?: ""
        val STAND_ON_GROUND = game.messages["STAND_ON_GROUND"] ?: ""
    }
}