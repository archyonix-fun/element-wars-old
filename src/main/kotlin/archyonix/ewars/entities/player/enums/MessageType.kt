package archyonix.ewars.entities.player.enums

import archyonix.ewars.shared.libs.Colors
import net.kyori.adventure.text.Component

enum class MessageType(
    val prefix: Component
) {
    INFO(Component.text("[I]: ", Colors.BLUE)),
    WARNING(Component.text("[W]: ", Colors.YELLOW)),
    ERROR(Component.text("[E]: ", Colors.RED)),
}