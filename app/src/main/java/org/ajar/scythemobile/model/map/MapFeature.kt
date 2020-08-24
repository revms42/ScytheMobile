package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.old.model.production.MapResourceType
import org.ajar.scythemobile.old.model.entity.Player
import org.ajar.scythemobile.old.model.faction.FactionMatModel

enum class SpecialFeature(override val featureName: String) : MapFeature {
    LAKE("Lake"),
    ENCOUNTER("Encounter"),
    TUNNEL("Tunnel"),
    FACTORY("Factory"),
    ANY("(any)")
}

class RiverFeature(override val featureName: String = "River", val direction: Direction) : MapFeature

class HomeBase(var player: PlayerInstance? = null) : MapFeature {

    private var model: StandardFactionMat? = null

    constructor(model: StandardFactionMat): this(null) {
        this.model = model
    }

    private val ownerName: String
        get() {
            return if(player != null) {
                player!!.factionMat.factionMat.matName
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