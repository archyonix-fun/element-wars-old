package archyonix.ewars.entities.structure

import archyonix.ewars.shared.libs.AppSchema
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class StructureVariant(
    val name: String,
    config: ConfigurationSection
) {
    private val schemaURI = config.getString("schema") ?: throw RuntimeException("Schema is null")

    val displayName = config.getString("displayName") ?: throw RuntimeException("DisplayName is null")
    val schema = AppSchema("config/structures/$name/$schemaURI")
    val buildPoints = config.getInt("buildPoints")
    val data = config.getConfigurationSection("data")
}