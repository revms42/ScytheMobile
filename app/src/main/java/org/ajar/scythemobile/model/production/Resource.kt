package org.ajar.scythemobile.model.production

import org.ajar.scythemobile.model.combat.CombatCard

class CrimeaCardResource(val card: CombatCard) : MapResource(MapResourceType.ANY)

open class MapResource(val typeMap: MapResourceType)

enum class MapResourceType : ResourceType {
    WOOD,
    FOOD,
    METAL,
    OIL,
    WORKER,
    ANY
}

enum class PlayerResourceType : ResourceType {
    COMBAT_CARD,
    POPULARITY,
    COIN,
    POWER
}

interface ResourceType {
}