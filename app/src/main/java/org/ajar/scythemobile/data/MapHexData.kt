package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.ajar.scythemobile.model.map.Direction

@Entity(tableName = MapHexData.TABLE_NAME)
data class MapHexData(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_LOC) val loc: Int,
        @ColumnInfo(name = COLUMN_TERRAIN_FEATURE) val terrain: Int,

        @Embedded val neighbors: Neighbors,
        @Embedded val rivers: Rivers = Rivers(),

        @ColumnInfo(name = COLUMN_ENCOUNTER) var encounter: Int? = null, // Null = no encounter, Int >= 0 == encounter/untriggerd, Int < 0 == encounter/triggered
        @ColumnInfo(name = COLUMN_TUNNEL) val tunnel: Boolean = false,
        @ColumnInfo(name = COLUMN_HOMEBASE) val faction: Int? = null,
        @ColumnInfo(name = Versioned.COLUMN_VERSION) override var version: Int = 0
) : Versioned {
    override fun toString(): String {
        return "H:$loc,$terrain,$neighbors,$rivers,$encounter,$tunnel,$faction,$version"
    }

    override fun toStringCompressed(): String {
        return "H:$loc,$encounter"
    }

    companion object {
        init {
            Versioned.addVersionedDeserializer(::fromString)
        }

        const val TABLE_NAME = "Map"
        const val COLUMN_LOC = "loc"

        const val COLUMN_TERRAIN_FEATURE = "feature_terrain"

        const val COLUMN_ENCOUNTER = "encounter"
        const val COLUMN_TUNNEL = "tunnel"
        const val COLUMN_HOMEBASE = "faction_home"

        fun fromString(str: String): MapHexData? {
            return if(str.startsWith("H:")) {
                val parts = str.subSequence(2 until str.length).split(",")

                if(parts.size > 2) {
                    MapHexData(
                            parts[0].toInt(),
                            parts[1].toInt(),
                            Neighbors.fromString(parts[2]),
                            Rivers.fromString(parts[3]),
                            parts[4].let { if(it == "null") null else it.toInt() },
                            parts[5].toBoolean(),
                            parts[6].let { if(it == "null") null else it.toInt() },
                            parts[7].toInt()
                    )
                } else {
                    ScytheDatabase.mapDao()?.getMapHex(parts[0].toInt())?.also { data ->
                        data.encounter = parts[1].let { if(it == "null") null else it.toInt() }
                    }
                }
            } else null
        }
    }
}

interface Navigatable<T> {
    fun getDirection(direction: Direction): T
    fun getOpposite(direction: Direction): T
}

data class Neighbors (
        @ColumnInfo(name = COLUMN_NEIGHBOR_NW) val nw: Int = -1,
        @ColumnInfo(name = COLUMN_NEIGHBOR_NE) val ne: Int = -1,
        @ColumnInfo(name = COLUMN_NEIGHBOR_E) val e: Int = -1,
        @ColumnInfo(name = COLUMN_NEIGHBOR_SE) val se: Int = -1,
        @ColumnInfo(name = COLUMN_NEIGHBOR_SW) val sw: Int = -1,
        @ColumnInfo(name = COLUMN_NEIGHBOR_W) val w: Int = -1
) : Navigatable<Int> {
    override fun getOpposite(direction: Direction): Int {
        return when(direction) {
            Direction.NW -> se
            Direction.NE -> sw
            Direction.E -> w
            Direction.SE -> nw
            Direction.SW -> ne
            Direction.W -> e
        }
    }

    override fun getDirection(direction: Direction): Int {
        return when(direction) {
            Direction.NW -> nw
            Direction.NE -> ne
            Direction.E -> e
            Direction.SE -> se
            Direction.SW -> sw
            Direction.W -> w
        }
    }

    fun asArray(): Array<Int> {
        return arrayOf(nw, ne, e, se, sw, w)
    }

    override fun toString(): String {
        return "$nw:$ne:$e:$se:$sw:$w"
    }

    companion object {
        const val COLUMN_NEIGHBOR_NW = "neighbor_nw"
        const val COLUMN_NEIGHBOR_NE = "neighbor_ne"
        const val COLUMN_NEIGHBOR_E = "neighbor_e"
        const val COLUMN_NEIGHBOR_SE = "neighbor_se"
        const val COLUMN_NEIGHBOR_SW = "neighbor_sw"
        const val COLUMN_NEIGHBOR_W = "neighbor_w"

        fun fromString(str: String) : Neighbors {
            val parts = str.split(":")
            return Neighbors(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3].toInt(), parts[4].toInt(), parts[5].toInt())
        }
    }
}

data class Rivers(
        @ColumnInfo(name = COLUMN_RIVER_NW) val riverNW: Boolean = false,
        @ColumnInfo(name = COLUMN_RIVER_NE) val riverNE: Boolean = false,
        @ColumnInfo(name = COLUMN_RIVER_E) val riverE: Boolean = false,
        @ColumnInfo(name = COLUMN_RIVER_SE) val riverSE: Boolean = false,
        @ColumnInfo(name = COLUMN_RIVER_SW) val riverSW: Boolean = false,
        @ColumnInfo(name = COLUMN_RIVER_W) val riverW: Boolean = false
) : Navigatable<Boolean> {
    override fun getOpposite(direction: Direction): Boolean {
        return when(direction) {
            Direction.NW -> riverSE
            Direction.NE -> riverSW
            Direction.E -> riverW
            Direction.SE -> riverNW
            Direction.SW -> riverNE
            Direction.W -> riverE
        }
    }

    override fun getDirection(direction: Direction): Boolean {
        return when(direction) {
            Direction.NW -> riverNW
            Direction.NE -> riverNE
            Direction.E -> riverE
            Direction.SE -> riverSE
            Direction.SW -> riverSW
            Direction.W -> riverW
        }
    }

    override fun toString(): String {
        return "$riverNW-$riverNE-$riverE-$riverSE-$riverSW-$riverW"
    }
    
    companion object {
        const val COLUMN_RIVER_NW = "river_nw"
        const val COLUMN_RIVER_NE = "river_ne"
        const val COLUMN_RIVER_E = "river_e"
        const val COLUMN_RIVER_SE = "river_se"
        const val COLUMN_RIVER_SW = "river_sw"
        const val COLUMN_RIVER_W = "river_w"

        fun fromString(str: String) : Rivers {
            val parts = str.split("-")
            return Rivers(parts[0].toBoolean(), parts[1].toBoolean(), parts[2].toBoolean(), parts[3].toBoolean(), parts[4].toBoolean(), parts[5].toBoolean())
        }
    }
}