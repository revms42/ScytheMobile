package org.ajar.scythemobile.old.model.production

import org.ajar.scythemobile.model.combat.CombatCard

class CrimeaCardResource(val card: CombatCard) : Resource {
    override val type: ResourceType = PlayerResourceType.COMBAT_CARD
}

open class MapResource(override val type: MapResourceType) : Resource
open class PlayerResource(override val type: PlayerResourceType) : Resource

interface Resource {
    val type: ResourceType
}

enum class MapResourceType : ResourceType {
    WOOD,
    FOOD,
    METAL,
    OIL,
    WORKER
}

enum class PlayerResourceType : ResourceType {
    COMBAT_CARD,
    POPULARITY,
    COIN,
    POWER
}

interface ResourceType