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
        @ColumnInfo(name = COLUMN_HOMEBASE) val faction: Int? = null
) {
    companion object {
        const val TABLE_NAME = "Map"
        const val COLUMN_LOC = "loc"

        const val COLUMN_TERRAIN_FEATURE = "feature_terrain"

        const val COLUMN_ENCOUNTER = "encounter"
        const val COLUMN_TUNNEL = "tunnel"
        const val COLUMN_HOMEBASE = "faction_home"
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

    companion object {
        const val COLUMN_NEIGHBOR_NW = "neighbor_nw"
        const val COLUMN_NEIGHBOR_NE = "neighbor_ne"
        const val COLUMN_NEIGHBOR_E = "neighbor_e"
        const val COLUMN_NEIGHBOR_SE = "neighbor_se"
        const val COLUMN_NEIGHBOR_SW = "neighbor_sw"
        const val COLUMN_NEIGHBOR_W = "neighbor_w"
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
    
    companion object {
        const val COLUMN_RIVER_NW = "river_nw"
        const val COLUMN_RIVER_NE = "river_ne"
        const val COLUMN_RIVER_E = "river_e"
        const val COLUMN_RIVER_SE = "river_se"
        const val COLUMN_RIVER_SW = "river_sw"
        const val COLUMN_RIVER_W = "river_w"
    }
}