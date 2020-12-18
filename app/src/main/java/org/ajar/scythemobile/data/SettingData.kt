package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SettingData.TABLE_NAME)
class SettingData(
        @PrimaryKey @ColumnInfo(name = COLUMN_NAME) val name: String,
        @ColumnInfo(name = COLUMN_VALUE) var value: String?
) {

    companion object {
        const val TABLE_NAME = "Settings"

        const val COLUMN_NAME = "name"
        const val COLUMN_VALUE = "value"
    }
}