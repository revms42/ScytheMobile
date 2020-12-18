package org.ajar.scythemobile.data

import androidx.room.*

@Dao
interface SettingDataDAO {
    @Query("SELECT * from ${SettingData.TABLE_NAME}")
    fun getAll(): List<SettingData>?

    @Query("SELECT ${SettingData.COLUMN_VALUE} FROM ${SettingData.TABLE_NAME} WHERE ${SettingData.COLUMN_NAME} = :name ")
    fun getValue(name: String): String?

    @Query("SELECT * FROM ${SettingData.TABLE_NAME} WHERE ${SettingData.COLUMN_NAME} = :name ")
    fun getSetting(name: String): SettingData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSetting(vararg settings: SettingData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSetting(vararg settings: SettingData)

    @Delete
    fun deleteSetting(vararg settings: SettingData)
}