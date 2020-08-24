package org.ajar.scythemobile.data

import androidx.room.*

@Dao
interface UnitDataDAO {
    @Query("SELECT * FROM ${UnitData.TABLE_NAME}")
    fun getUnits(): List<UnitData>?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_INDEX} = :id LIMIT 1")
    fun getUnit(id: Int): UnitData?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_OWNER} = :owner AND ${UnitData.COLUMN_TYPE} = :type")
    fun getUnitsForPlayer(owner: Int, type: Int): List<UnitData>?

    @Query("SELECT * FROM ${UnitData.TABLE_NAME} WHERE ${UnitData.COLUMN_LOCATION} = :loc")
    fun getUnitsAtLocation(loc: Int): List<UnitData>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUnit(unit: UnitData)

    @Delete
    fun removeUnit(vararg unit: UnitData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUnit(vararg unit: UnitData)
}