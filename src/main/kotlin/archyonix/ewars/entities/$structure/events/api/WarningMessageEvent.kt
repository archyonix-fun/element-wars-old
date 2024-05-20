package archyonix.ewars.entities.`$structure`.events.api

import archyonix.ewars.shared.libs.Colors
import net.kyori.adventure.text.Component

abstract class WarningMessageEvent {
    private var warningMessage: Component? = null

    fun setWarningMessage(message: String) {
        this.warningMessage = Component.text(message, Colors.TEXT)
    }

    fun setWarningMessage(message: Component) {
        this.warningMessage = message
    }

    fun getWarningMessage() = warningMessage
        ?: Component.text("Что-то пошло не так")
}