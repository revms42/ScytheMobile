package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = UnitData.TABLE_NAME)
data class UnitData(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_INDEX) var id: Int,
        @ColumnInfo(name = COLUMN_OWNER) override var owner: Int,
        @ColumnInfo(name = COLUMN_LOCATION) override var loc: Int,
        @ColumnInfo(name = COLUMN_TYPE) var type: Int,
        @ColumnInfo(name = COLUMN_STATE) var state: Int = -1,
        @ColumnInfo(name = COLUMN_SUB_TYPE) var subType: Int = -1
) : Mappable {

    companion object {
        const val TABLE_NAME = "Unit"

        const val COLUMN_INDEX = "id"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_LOCATION = "loc"
        const val COLUMN_TYPE = "type"
        const val COLUMN_STATE = "state"
        const val COLUMN_SUB_TYPE = "sub_type"
    }
}