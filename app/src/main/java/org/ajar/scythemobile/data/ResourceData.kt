package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ResourceData(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_INDEX) var id: Int,
        @ColumnInfo(name = COLUMN_POSITION) var pos: Int,
        @ColumnInfo(name = COLUMN_OWNER) var owner: Int,
        @ColumnInfo(name = COLUMN_TYPE) var type: Int,
        @ColumnInfo(name = COLUMN_VALUE) var value: Int = 1
) {

    companion object {
        const val TABLE_NAME = "Resource"

        const val COLUMN_INDEX = "id"
        const val COLUMN_POSITION = "pos"
        const val COLUMN_OWNER = "own" // Each resource will have either a position or an owner but not both.
        const val COLUMN_TYPE = "pos"
        const val COLUMN_VALUE = "val"
    }
}