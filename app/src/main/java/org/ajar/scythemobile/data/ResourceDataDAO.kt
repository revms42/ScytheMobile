package org.ajar.scythemobile.data

import androidx.room.*

@Dao
interface ResourceDataDAO {
    @Query("SELECT * FROM ${ResourceData.TABLE_NAME}")
    fun getResources(): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_INDEX} = :id LIMIT 1")
    fun getResource(id: Int): ResourceData?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_POSITION} = :pos")
    fun getResourcesAt(pos: Int): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_TYPE} = :type")
    fun getResourcesOfType(type: Int): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_TYPE} = :type AND ${ResourceData.COLUMN_OWNER} = :owner")
    fun getOwnedResourcesOfType(type: Int, owner: Int): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_POSITION} = :pos and ${ResourceData.COLUMN_TYPE} = :type")
    fun getResourcesAtPosOfType(pos: Int, type: Int): List<ResourceData>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addResource(vararg resourceData: ResourceData)

    @Delete
    fun removeResource(vararg setting: ResourceData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateResource(vararg setting: ResourceData)
}