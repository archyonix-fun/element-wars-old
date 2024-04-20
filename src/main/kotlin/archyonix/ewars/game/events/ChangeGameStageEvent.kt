package archyonix.ewars.game.events

import archyonix.ewars.game.enums.GameStage

class ChangeGameStageEvent(
    val stage: GameStage
): GameEvent