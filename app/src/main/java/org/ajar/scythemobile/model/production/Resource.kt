package org.ajar.scythemobile.model.production

import org.ajar.scythemobile.model.combat.CombatCard

class CrimeaCardResource(val card: CombatCard) : Resource(ResourceType.ANY)

open class Resource(val type: ResourceType)

enum class ResourceType {
    WOOD,
    FOOD,
    METAL,
    OIL,
    WORKER,
    ANY
}