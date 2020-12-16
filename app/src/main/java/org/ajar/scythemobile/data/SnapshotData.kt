package org.ajar.scythemobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SnapshotData.TABLE_NAME)
data class SnapshotData(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_INDEX) var id: Int,
        @ColumnInfo(name = COLUMN_TYPE) var type: Int,
        @ColumnInfo(name = COLUMN_OBJECT_ID) var objId: Int,
        @ColumnInfo(name = COLUMN_VERSION) var version: Int
) {
    companion object {
        const val TABLE_NAME = "db_snapshot"

        const val COLUMN_INDEX = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_OBJECT_ID = "obj_id"
        const val COLUMN_VERSION = "obj_ver"
    }
}

enum class SnapshotType(val columnId: Int, val dataType: Class<out Versioned>) {
    PLAYER_DATA(0, PlayerData::class.java) {
        override fun getId(ver: Versioned): Int {
            return (ver as PlayerData).id
        }

        override fun getValue(data: SnapshotData): Versioned? {
            return ScytheDatabase.playerDao()?.getPlayer(data.objId)
        }

        override fun getAllValues(): List<Versioned>? {
            return ScytheDatabase.playerDao()?.getPlayers()
        }

        override fun updateValue(versioned: Versioned) {
            ScytheDatabase.playerDao()?.updatePlayer(versioned as PlayerData)
        }
    },
    MAP_HEX_DATA(1, MapHexData::class.java) {
        override fun getId(ver: Versioned): Int {
            return (ver as MapHexData).loc
        }

        override fun getValue(data: SnapshotData): Versioned? {
            return ScytheDatabase.mapDao()?.getMapHex(data.objId)
        }

        override fun getAllValues(): List<Versioned>? {
            return ScytheDatabase.mapDao()?.getMap()
        }

        override fun updateValue(versioned: Versioned) {
            ScytheDatabase.mapDao()?.updateMapHex(versioned as MapHexData)
        }
    },
    RESOURCE_DATA(2, ResourceData::class.java) {
        override fun getId(ver: Versioned): Int {
            return (ver as ResourceData).id
        }

        override fun getValue(data: SnapshotData): Versioned? {
            return ScytheDatabase.resourceDao()?.getResource(data.objId)
        }

        override fun getAllValues(): List<Versioned>? {
            return ScytheDatabase.resourceDao()?.getResources()
        }

        override fun updateValue(versioned: Versioned) {
            ScytheDatabase.resourceDao()?.updateResource(versioned as ResourceData)
        }
    },
    UNIT_DATA(3, UnitData::class.java) {
        override fun getId(ver: Versioned): Int {
            return (ver as UnitData).id
        }

        override fun getValue(data: SnapshotData): Versioned? {
            return ScytheDatabase.unitDao()?.getUnit(data.objId)
        }

        override fun getAllValues(): List<Versioned>? {
            return ScytheDatabase.unitDao()?.getUnits()
        }

        override fun updateValue(versioned: Versioned) {
            ScytheDatabase.unitDao()?.updateUnit(versioned as UnitData)
        }
    };

    abstract fun getId(ver: Versioned): Int
    abstract fun getValue(data: SnapshotData) : Versioned?
    abstract fun getAllValues(): List<Versioned>?
    abstract fun updateValue(versioned: Versioned)

    companion object {
        fun <A: Versioned> typeFor(versioned: A) : Int {
            return values().firstOrNull { value -> value.dataType == versioned::class.java }?.columnId?: -1
        }

        fun <A: Versioned> getIdFor(versioned: A) : Int {
            return values().firstOrNull { value -> value.dataType == versioned::class.java }?.getId(versioned)?: -1
        }

        fun <A: Versioned> getAllFor(type: Class<A>) : List<A>? {
            return values().firstOrNull { value -> value.dataType == type }?.getAllValues() as List<A>?
        }

        fun <A: Versioned> update(versioned: A) {
            values().firstOrNull { value -> value.dataType == versioned::class.java }?.updateValue(versioned)
        }
    }
}