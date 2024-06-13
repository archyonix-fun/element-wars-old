package archyonix.ewars.features.action.executors.weapon_melee

import archyonix.ewars.features.classlibrary.ClassLibrary
import archyonix.ewars.features.pvp.ShockWave

@ClassLibrary.RegisteredClass("SWORD_MELEE_ATTACK_VERTICAL", ClassLibrary.Type.ACTION_EXECUTOR)
class SwordVerticalAttackExecutor(
    properties: Map<String, Any>
) : SwordAttackExecutor(
    ShockWave.Direction.VERTICAL,
    properties
)