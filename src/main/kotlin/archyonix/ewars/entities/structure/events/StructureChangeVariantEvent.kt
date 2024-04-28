package archyonix.ewars.entities.structure.events

import archyonix.ewars.entities.structure.Structure

data class StructureChangeVariantEvent(
    val structure: Structure<*>
): StructureEvent
