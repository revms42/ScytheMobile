package org.ajar.scythemobile.data

import androidx.room.*

@Dao
interface MapHexDao {
    @Query("SELECT * FROM ${MapHexData.TABLE_NAME}")
    fun getMap(): List<MapHexData>?

    @Query("SELECT * FROM ${MapHexData.TABLE_NAME} WHERE ${MapHexData.COLUMN_LOC} = :loc LIMIT 1")
    fun getMapHex(loc: Int): MapHexData?

    @Query("SELECT * FROM ${MapHexData.TABLE_NAME} WHERE ${MapHexData.COLUMN_TUNNEL} = 'true'")
    fun getTunnels(): List<MapHexData>?

    @Query("SELECT * FROM ${MapHexData.TABLE_NAME} WHERE ${MapHexData.COLUMN_TERRAIN_FEATURE} = :feature")
    fun getFeatureHexs(feature: Int): List<MapHexData>?

    @Query("SELECT * FROM ${MapHexData.TABLE_NAME} WHERE ${MapHexData.COLUMN_HOMEBASE} IN (:faction)")
    fun getHomeBase(vararg faction: Int): List<MapHexData>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMapHex(vararg hex: MapHexData)

    @Delete
    fun removeMapHex(vararg hex: MapHexData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateMapHex(vararg hex: MapHexData)
}