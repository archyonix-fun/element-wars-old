package archyonix.ewars.entities.`$structure`.events.api

import archyonix.ewars.shared.libs.Colors
import net.kyori.adventure.text.Component

interface ErrorMessageEvent {
    var errorMessage: Component?

    fun setErrorMessage(message: String) {
        this.errorMessage = Component.text(message, Colors.TEXT)
    }

    fun setErrorMessage(message: Component) {
        this.errorMessage = message
    }

    fun getErrorMessage() = errorMessage
        ?: Component.text("Что-то пошло не так")
}