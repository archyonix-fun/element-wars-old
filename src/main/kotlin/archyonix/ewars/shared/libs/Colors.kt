package archyonix.ewars.shared.libs

import net.kyori.adventure.text.format.TextColor

class Colors {
    companion object {
        val BLUE = TextColor.fromHexString("#0cb2aa")
        val YELLOW = TextColor.fromHexString("#d7a116")
        val RED = TextColor.fromHexString("#c51c29")
        val TEXT = TextColor.fromHexString("#a2a2a2")

        fun colors(): Set<String> =
            Colors::class.java
                .fields.map { it.name.lowercase() }
                .toSet()

        fun fromName(name: String): TextColor? {
            return try {
                Colors::class.java
                    .getField(name.uppercase())
                    .get(null) as TextColor
            } catch (_: Exception) {
                null
            }
        }
    }
}