package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.NaturalResourceType
import org.ajar.scythemobile.model.faction.FactionMat

interface MapFeature {
    fun applyToData(builder: MapHexBuilder)
}

enum class TerrainFeature(var displayName: String, var desc: String, val resource: NaturalResourceType? = null) : MapFeature {
    FOREST("Forest", "", NaturalResourceType.WOOD),
    FIELD("Field", "", NaturalResourceType.FOOD),
    MOUNTAIN("Mountain","", NaturalResourceType.METAL),
    TUNDRA("Tundra", "", NaturalResourceType.OIL),
    VILLAGE("Village", ""),
    LAKE("Lake", ""),
    FACTORY("Factory", "");

    override fun applyToData(builder: MapHexBuilder) {
        builder.terrain = this.ordinal
    }

    companion object {
        fun valueOf(ordinal: Int): TerrainFeature {
            return values()[ordinal]
        }
    }
}

class RiverFeature(val direction: Direction) : MapFeature {
    override fun applyToData(builder: MapHexBuilder) {
        builder.addRiver(direction)
    }
}

class HomeBase(val faction: FactionMat) : MapFeature {
    override fun applyToData(builder: MapHexBuilder) {
        builder.faction = faction.id
    }

}

enum class SpecialFeature : MapFeature {
    TUNNEL {
        override fun applyToData(builder: MapHexBuilder) {
            builder.tunnel = true
        }
    },
    ENCOUNTER {
        override fun applyToData(builder: MapHexBuilder) {
            TODO("Encounter")
        }
    }
}