package archyonix.ewars.shared.libs

import net.kyori.adventure.text.format.TextColor
import java.lang.reflect.Modifier

class Colors {
    companion object {
        val BLUE = TextColor.fromHexString("#0cb2aa")
        val YELLOW = TextColor.fromHexString("#d7a116")
        val RED = TextColor.fromHexString("#c51c29")
        val TEXT = TextColor.fromHexString("#a2a2a2")

        fun colors(): Set<String> =
            Colors::class.java
                .declaredFields
                .filter { Modifier.isStatic(it.modifiers) }
                .filter { it.name != "Companion" }
                .map { it.name.lowercase() }
                .toSet()

        fun fromName(name: String): TextColor {
            val field = Colors::class.java.getDeclaredField(name.uppercase())
            field.isAccessible = true
            val result = field.get(null) as TextColor
            field.isAccessible = false
            return result
        }
    }
}