package archyonix.ewars.features.action.executors.weapon_melee

import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.features.pvp.ShockWave

@ClassLibrary.RegisteredClass("SWORD_MELEE_ATTACK_HORIZONTAL", ClassLibrary.Type.ACTION_EXECUTOR)
class SwordHorizontalAttackExecutor(
    properties: Map<String, Any>
) : SwordAttackExecutor(
    ShockWave.Direction.HORIZONTAL,
    properties
)