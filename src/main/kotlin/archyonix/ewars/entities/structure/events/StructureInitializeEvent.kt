package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure

data class StructureInitializeEvent(
    val structure: Structure<*>
) : StructureEvent
