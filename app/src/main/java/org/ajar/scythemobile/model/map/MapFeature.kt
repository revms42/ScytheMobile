package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.faction.FactionMat

interface MapFeature {
    val displayable: Int?
    fun applyToData(builder: MapHexBuilder)
}

enum class TerrainFeature(var displayName: String, var desc: String, override val displayable: Int? = null, val resource: NaturalResourceType? = null) : MapFeature {
    FOREST("Forest", "", R.drawable.hex_forest, NaturalResourceType.WOOD),
    FIELD("Field", "", R.drawable.hex_field, NaturalResourceType.FOOD),
    MOUNTAIN("Mountain","", R.drawable.hex_mountain, NaturalResourceType.METAL),
    TUNDRA("Tundra", "", R.drawable.hex_tundra, NaturalResourceType.OIL),
    VILLAGE("Village", "", R.drawable.hex_town),
    LAKE("Lake", "", R.drawable.hex_lake),
    FACTORY("Factory", "", R.drawable.hex_factory);

    override fun applyToData(builder: MapHexBuilder) {
        builder.terrain = this.ordinal
    }

    val productionFeature: Boolean
        get() = this.resource != null || this == VILLAGE

    companion object {
        fun valueOf(ordinal: Int): TerrainFeature {
            return values()[ordinal]
        }
    }
}

class RiverFeature(val direction: Direction) : MapFeature {
    override val displayable: Int?
        get() {
            return when(direction) {
                Direction.NW -> R.drawable.river_nw
                Direction.NE -> R.drawable.river_ne
                Direction.E -> R.drawable.river_e
                Direction.SE -> R.drawable.river_se
                Direction.SW -> R.drawable.river_sw
                Direction.W -> R.drawable.river_w
            }
        }
    override fun applyToData(builder: MapHexBuilder) {
        builder.addRiver(direction)
    }
}

class HomeBase(val faction: FactionMat) : MapFeature {
    override val displayable: Int?
        get() = if(faction.symbol != 0) faction.symbol else null

    override fun applyToData(builder: MapHexBuilder) {
        builder.faction = faction.id
    }

    companion object {
        fun valueOf(id: Int): HomeBase {
            return HomeBase(FactionMat[id]!!)
        }
    }
}

enum class SpecialFeature : MapFeature {
    TUNNEL {
        override val displayable: Int = R.drawable.ic_tunnel

        override fun applyToData(builder: MapHexBuilder) {
            builder.tunnel = true
        }
    },
    ENCOUNTER {
        override val displayable: Int = R.drawable.ic_encounter

        override fun applyToData(builder: MapHexBuilder) {
            builder.encounter = EncounterDeck.currentDeck.drawCard()?.id
        }
    }
}