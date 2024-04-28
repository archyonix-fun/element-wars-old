package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure

data class StructureBuildEvent(
    val structure: Structure<*>
): StructureEvent
