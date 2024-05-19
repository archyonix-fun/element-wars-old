package archyonix.ewars.entities.`$structure`.controllers

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.features.classlibrary.ClassLibrary
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@ClassLibrary.RegisteredClass(name = "EXTENSIONS", ClassLibrary.Type.STRUCTURE_CONTROLLER)
open class ExtensionsController(
    config: ConfigurationSection
) : Structure.Config.Controller(config) {

}