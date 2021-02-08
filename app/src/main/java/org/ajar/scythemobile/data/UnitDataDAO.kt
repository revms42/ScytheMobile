package org.ajar.scythemobile.data

import androidx.room.*
import org.ajar.scythemobile.model.entity.UnitType

@Dao
interface UnitDataDAO {
    @Query("SELECT * FROM ${UnitData.TABLE_NAME}")
    fun getUnits(): List<UnitData>?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_INDEX} = :id LIMIT 1")
    fun getUnit(id: Int): UnitData?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_OWNER} = :owner AND ${UnitData.COLUMN_TYPE} = :type")
    fun getUnitsForPlayer(owner: Int, type: Int): List<UnitData>?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_OWNER} = :owner AND ${UnitData.COLUMN_TYPE} in (:type)")
    fun getUnitsForPlayer(owner: Int, type: List<Int>): List<UnitData>?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_LOCATION} = :loc")
    fun getUnitsAtLocation(loc: Int): List<UnitData>?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_LOCATION} = :loc AND ${UnitData.COLUMN_OWNER} = :owner AND ${UnitData.COLUMN_TYPE} IN (:types)")
    fun getSpecificUnitsAtLoc(loc: Int, owner: Int, types: List<Int> = listOf(UnitType.CHARACTER.ordinal, UnitType.MECH.ordinal, UnitType.WORKER.ordinal)): List<UnitData>?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_INDEX} IN (:ids)")
    fun getUnitsFromList(ids: List<Int>): List<UnitData>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUnit(vararg unit: UnitData)

    @Delete
    fun removeUnit(vararg unit: UnitData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUnit(vararg unit: UnitData)

    fun updateUnitAndIncrement(vararg unit: UnitData) {
        updateUnit(*unit.map { data -> data.version += 1 ; data }.toTypedArray())
    }
}