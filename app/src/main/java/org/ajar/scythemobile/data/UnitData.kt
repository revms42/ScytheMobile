package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.ajar.scythemobile.data.Versioned.Companion.COLUMN_VERSION

@Entity(tableName = UnitData.TABLE_NAME)
data class UnitData(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_INDEX) var id: Int,
        @ColumnInfo(name = COLUMN_OWNER) override var owner: Int,
        @ColumnInfo(name = COLUMN_LOCATION) override var loc: Int,
        @ColumnInfo(name = COLUMN_TYPE) var type: Int,
        @ColumnInfo(name = COLUMN_STATE) var state: Int = -1,
        @ColumnInfo(name = COLUMN_SUB_TYPE) var subType: Int = -1,
        @ColumnInfo(name = COLUMN_VERSION) override var version: Int = 0
) : Mappable, Versioned {
    override fun toString(): String {
        return "U:$id,$owner,$loc,$type,$state,$subType,$version"
    }

    override fun toStringCompressed(): String {
        return "U:$id,$loc,$state,$version"
    }

    companion object {
        init {
            Versioned.addVersionedDeserializer(::fromString)
        }

        const val TABLE_NAME = "Unit"

        const val COLUMN_INDEX = "id"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_LOCATION = "loc"
        const val COLUMN_TYPE = "type"
        const val COLUMN_STATE = "state"
        const val COLUMN_SUB_TYPE = "sub_type"

        fun fromString(str: String): UnitData? {
            return if(str.startsWith("U:")) {
                val parts = str.subSequence(2 until str.length).split(",")

                if(parts.size > 4) {
                    UnitData(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3].toInt(), parts[4].toInt(), parts[5].toInt(), parts[6].toInt())
                } else {
                    ScytheDatabase.unitDao()?.getUnit(parts[0].toInt())?.also { data ->
                        data.loc = parts[1].toInt()
                        data.state = parts[2].toInt()
                        data.version = parts[3].toInt()
                    }
                }
            } else null
        }
    }
}