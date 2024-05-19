package archyonix.ewars.entities.`$structure`.controllers

import archyonix.ewars.entities.`$structure`.Structure
import archyonix.ewars.features.classlibrary.ClassLibrary
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

@ClassLibrary.RegisteredClass(name = "WALLS", ClassLibrary.Type.STRUCTURE_CONTROLLER)
class WallController(
    config: ConfigurationSection
) : Structure.Config.Controller("WALLS", setOf("EXTENSIONS")) {

}