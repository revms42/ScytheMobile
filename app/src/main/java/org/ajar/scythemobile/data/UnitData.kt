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
        @ColumnInfo(name = COLUMN_STATE) var state: Int,
        @ColumnInfo(name = COLUMN_SUB_TYPE) var subType: Int
) {

    companion object {
        const val TABLE_NAME = "UnitData"

        const val COLUMN_INDEX = "ID"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_LOCATION = "loc"
        const val COLUMN_TYPE = "type"
        const val COLUMN_STATE = "state"
        const val COLUMN_SUB_TYPE = "sub_type"
    }
}