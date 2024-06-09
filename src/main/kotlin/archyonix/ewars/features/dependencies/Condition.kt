package archyonix.ewars.features.dependencies

class Condition(
    val type: Dependence.DependenceType,
    val id: String
) {
    companion object {
        fun fromString(data: String): Condition {
            val split = data.split(":")
            val type = Dependence.DependenceType.valueOf(split[0].uppercase())
            val id = split[1]
            return Condition(type, id)
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(other !is Condition) return false
        return other.type == type && other.id == id
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}