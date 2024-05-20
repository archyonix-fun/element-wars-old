package archyonix.ewars.shared.libs.config

import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

abstract class Properties(
    config: ConfigurationSection? = null
) {
    private val properties: MutableMap<String, Any> = HashMap()

    init {
        if (config != null) importProperties(config)
    }

    private fun importProperties(config: ConfigurationSection) {
        val section = config.getConfigurationSection("properties") ?: return
        for (key in section.getKeys(false)) {
            setProperty(key, section.get(key))
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getProperty(key: String) = properties[key] as T?

    fun setProperty(key: String, value: Any?) {
        if (value == null) properties.remove(key)
        else properties[key] = value
    }
}