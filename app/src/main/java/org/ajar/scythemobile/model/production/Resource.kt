package org.ajar.scythemobile.model.production

import org.ajar.scythemobile.model.combat.CombatCard

class CrimeaCardResource(type: ResourceType, val card: CombatCard) : Resource(type)

open class Resource(val type: ResourceType)

enum class ResourceType {
    WOOD,
    FOOD,
    METAL,
    OIL,
    WORKER
}