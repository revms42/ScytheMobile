package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UnitData(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_INDEX) var id: Int,
        @ColumnInfo(name = COLUMN_OWNER) var owner: Int,
        @ColumnInfo(name = COLUMN_LOCATION) var loc: Int,
        @ColumnInfo(name = COLUMN_TYPE) var type: Int,
        @ColumnInfo(name = COLUMN_CARRIED_WOOD) var wood: Int,
        @ColumnInfo(name = COLUMN_CARRIED_METAL) var metal: Int,
        @ColumnInfo(name = COLUMN_CARRIED_FOOD) var food: Int,
        @ColumnInfo(name = COLUMN_CARRIED_OIL) var oil: Int,
        @ColumnInfo(name = COLUMN_CARRIED_WORKER) var worker: Int,
        @ColumnInfo(name = COLUMN_CARRIED_MECH) var mech: Int
) {

    companion object {
        const val TABLE_NAME = "UnitData"

        const val COLUMN_INDEX = "ID"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_LOCATION = "loc"
        const val COLUMN_TYPE = "type"
        const val COLUMN_CARRIED_WOOD = "wood"
        const val COLUMN_CARRIED_METAL = "metal"
        const val COLUMN_CARRIED_FOOD = "food"
        const val COLUMN_CARRIED_OIL = "oil"
        const val COLUMN_CARRIED_WORKER = "worker"
        const val COLUMN_CARRIED_MECH = "mech"
    }
}