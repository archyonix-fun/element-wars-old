package archyonix.ewars.entities.resource.api

import archyonix.ewars.entities.resource.Resource
import archyonix.ewars.game
import net.minestom.server.coordinate.Point
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.properties.Delegates

abstract class Extractor(
    config: ConfigurationSection
) {
    fun instance() = game.instance
    private var blocks: Set<String>
    private var unions: Set<String>
    var swap: String
    var repair by Delegates.notNull<Boolean>()
    var seconds by Delegates.notNull<Int>()
    var resource: Resource

    init {
        val blocks = config.getString("blocks") ?: throw RuntimeException("Field blocks is null")
        this.blocks = blocks.split(",").toSet()

        val unions = config.getString("unions") ?: throw RuntimeException("Field unions is null")
        this.unions = unions.split(",").toSet()

        val resource = config.getString("resource") ?: throw RuntimeException("Field resource is null")
        this.resource = Resource.valueOf(resource.uppercase())

        this.swap = config.getString("swap") ?: throw RuntimeException("Field swap is null")
        this.repair = config.getBoolean("recovery.enabled")
        this.seconds = config.getInt("recovery.seconds")
    }

    fun containsBlock(name: String) =
        if (blocks.contains("any")) true
        else blocks.contains(name)

    fun containsToolUnion(union: String) =
        if (unions.contains("any")) true
        else unions.contains(union)

    abstract fun run(point: Point, toolGroup: String): Boolean
}