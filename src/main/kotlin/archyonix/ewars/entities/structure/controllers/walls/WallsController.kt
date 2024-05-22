package archyonix.ewars.entities.structure.controllers.walls

import archyonix.ewars.entities.structure.Structure
import archyonix.ewars.entities.structure.controllers.extensions.ExtensionsController
import archyonix.ewars.features.classlibrary.ClassLibrary
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

class WallsController(
    structure: Structure,
    config: Config
) : Structure.Controller<WallsController, WallsController.Config>(structure, config) {

    // Child classes
    @ClassLibrary.RegisteredClass(name = "WALLS", ClassLibrary.Type.STRUCTURE_CONTROLLER)
    class Config(
        configurationSection: ConfigurationSection
    ) : Structure.Controller.Config<WallsController, Config>("WALLS", setOf("EXTENSIONS")) {
        private var extensions: ExtensionsController.Config? = null
        fun extensions(): ExtensionsController.Config = extensions!!
        override fun init(structureConfig: Structure.Config) {
            this.extensions = structureConfig.getController(ExtensionsController.Config::class.java)
                ?: throw RuntimeException("Dependency not found: EXTENSIONS")
        }

        override fun getController(structure: Structure): WallsController =
            WallsController(structure, this)
    }
}