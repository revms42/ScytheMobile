package org.ajar.scythemobile.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayerDataDAO {
    @Query("SELECT * FROM ${PlayerData.TABLE_NAME}")
    fun getPlayers(): List<PlayerData>?

    @Query("SELECT * FROM ${PlayerData.TABLE_NAME} WHERE ${PlayerData.COLUMN_NAME} = :name LIMIT 1")
    fun observePlayer(name: String): LiveData<PlayerData?>

    @Query("SELECT ${PlayerData.COLUMN_PLAYER_MAT} FROM ${PlayerData.TABLE_NAME} WHERE ${PlayerData.COLUMN_INDEX} = :id LIMIT 1")
    fun observePlayerMat(id: Int): LiveData<PlayerMatData?>

    @Query("SELECT ${PlayerData.COLUMN_FACTION_MAT} FROM ${PlayerData.TABLE_NAME} WHERE ${PlayerData.COLUMN_INDEX} = :id LIMIT 1")
    fun observeFactionMat(id: Int): LiveData<FactionMatData?>

    @Query("SELECT * FROM ${PlayerData.TABLE_NAME} WHERE ${PlayerData.COLUMN_NAME} = :name LIMIT 1")
    fun getPlayer(name: String): PlayerData?

    @Query("SELECT * FROM ${PlayerData.TABLE_NAME} WHERE ${PlayerData.COLUMN_INDEX} = :id LIMIT 1")
    fun getPlayer(id: Int): PlayerData?

    @Query("SELECT ${PlayerData.COLUMN_PLAYER_MAT} FROM ${PlayerData.TABLE_NAME} WHERE ${PlayerData.COLUMN_INDEX} = :id LIMIT 1")
    fun getPlayerMat(id: Int): PlayerMatData?

    @Query("SELECT ${PlayerData.COLUMN_FACTION_MAT} FROM ${PlayerData.TABLE_NAME} WHERE ${PlayerData.COLUMN_INDEX} = :id LIMIT 1")
    fun getFactionMat(id: Int): FactionMatData?

    @Query("SELECT ${PlayerData.COLUMN_POWER} FROM ${PlayerData.TABLE_NAME} ORDER BY ${PlayerData.COLUMN_POWER} LIMIT 1")
    fun getHighestPower(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayer(playerData: PlayerData)

    @Delete
    fun removePlayer(vararg setting: PlayerData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePlayer(vararg setting: PlayerData)
}