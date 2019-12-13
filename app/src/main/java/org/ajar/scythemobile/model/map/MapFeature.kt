package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.production.MapResourceType
import org.ajar.scythemobile.model.entity.Player
import org.ajar.scythemobile.model.faction.FactionMatModel

enum class SpecialFeature(override val featureName: String) : MapFeature {
    LAKE("Lake"),
    ENCOUNTER("Encounter"),
    TUNNEL("Tunnel"),
    FACTORY("Factory"),
    ANY("(any)")
}

class RiverFeature(override val featureName: String = "River", val direction: Direction) : MapFeature

class HomeBase(var player: Player? = null) : MapFeature {

    private var model: FactionMatModel? = null

    constructor(model: FactionMatModel): this(null) {
        this.model = model
    }

    private val ownerName: String
        get() {
            return if(player != null) {
                player!!.factionMat.model.matName
            } else {
                model!!.matName
            }
        }

    override val featureName: String
        get() {
            return "$ownerName Home Base"
        }
}

enum class ResourceFeature(override val featureName: String, val mapResource: MapResourceType) : MapFeature {
    MOUNTAIN("Mountain", MapResourceType.METAL),
    TUNDRA("Tundra", MapResourceType.OIL),
    FARM("Farm", MapResourceType.FOOD),
    FOREST("Forest", MapResourceType.WOOD),
    VILLAGE("Village", MapResourceType.WORKER)
}

interface MapFeature {
    val featureName: String
}