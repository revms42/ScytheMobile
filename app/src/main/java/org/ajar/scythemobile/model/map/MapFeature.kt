package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.production.ResourceType
import org.ajar.scythemobile.model.entity.Player

enum class SpecialFeature(override val featureName: String) : MapFeature {
    LAKE("Lake"),
    ENCOUNTER("Encounter"),
    TUNNEL("Tunnel"),
    FACTORY("Factory"),
    ANY("(any)")
}

class RiverFeature(override val featureName: String = "River", val direction: Direction) : MapFeature

class HomeBase(val player: Player) : MapFeature {
    override val featureName: String = "${player.factionMat.model.matName} Home Base"
}

enum class ResourceFeature(override val featureName: String, val resource: ResourceType) : MapFeature {
    MOUNTAIN("Mountain", ResourceType.METAL),
    TUNDRA("Tundra", ResourceType.OIL),
    FARM("Farm", ResourceType.FOOD),
    FOREST("Forest", ResourceType.WOOD),
    VILLAGE("Village", ResourceType.WORKER)
}

interface MapFeature {
    val featureName: String
}