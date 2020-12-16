package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ResourceData.TABLE_NAME)
data class ResourceData(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_INDEX) var id: Int,
        @ColumnInfo(name = COLUMN_POSITION) override var loc: Int,
        @ColumnInfo(name = COLUMN_OWNER) override var owner: Int,
        @ColumnInfo(name = COLUMN_TYPE) var type: Int,
        @ColumnInfo(name = COLUMN_VALUE) var value: Int = 1,
        @ColumnInfo(name = Versioned.COLUMN_VERSION) override var version: Int = 0
) : Mappable, Versioned {
    override fun toString(): String {
        return "R:$id,$loc,$owner,$type,$value,$version"
    }

    override fun toStringCompressed(): String {
        return "R:$id,$loc,$owner,$version"
    }

    companion object {
        init {
            Versioned.addVersionedDeserializer(::fromString)
        }

        const val TABLE_NAME = "Resource"

        const val COLUMN_INDEX = "id"
        const val COLUMN_POSITION = "pos"
        const val COLUMN_OWNER = "own" // Each resource will have either a position or an owner but not both.
        const val COLUMN_TYPE = "type"
        const val COLUMN_VALUE = "val"

        fun fromString(str: String): ResourceData? {
            return if(str.startsWith("R:")) {
                val parts = str.subSequence(2 until str.length).split(",")

                if(parts.size > 4) {
                    ResourceData(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3].toInt(), parts[4].toInt(), parts[5].toInt())
                } else {
                    ScytheDatabase.resourceDao()?.getResource(parts[0].toInt())?.also { data ->
                        data.loc = parts[1].toInt()
                        data.owner = parts[2].toInt()
                        data.version = parts[3].toInt()
                    }
                }
            } else null
        }
    }
}