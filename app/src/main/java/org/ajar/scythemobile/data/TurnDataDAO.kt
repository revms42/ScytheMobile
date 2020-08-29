package org.ajar.scythemobile.data

import androidx.room.*

@Dao
interface TurnDataDAO {
    @Query("SELECT * FROM ${TurnData.TABLE_NAME}")
    fun getTurns(): List<TurnData>?

    @Query("SELECT * FROM ${TurnData.TABLE_NAME} ORDER BY ${TurnData.COLUMN_INDEX} DESC LIMIT 1")
    fun getCurrentTurn(): TurnData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTurn(vararg turn: TurnData)

    @Delete
    fun removeTurn(vararg turn: TurnData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTurn(vararg unit: TurnData)
}