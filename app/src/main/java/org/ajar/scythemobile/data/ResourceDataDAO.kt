package org.ajar.scythemobile.data

import androidx.room.*

@Dao
interface ResourceDataDAO {
    @Query("SELECT * FROM ${ResourceData.TABLE_NAME}")
    fun getResources(): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_INDEX} = :id LIMIT 1")
    fun getResource(id: Int): ResourceData?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_POSITION} = :loc")
    fun getResourcesAt(loc: Int): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_TYPE} = :type")
    fun getResourcesOfType(type: Int): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_TYPE} in (:type) AND ${ResourceData.COLUMN_POSITION} = :loc")
    fun getChosenResourcesFromLocations(type: List<Int>, loc: Int): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_TYPE} IN (:type) AND ${ResourceData.COLUMN_OWNER} = :owner")
    fun getOwnedResourcesOfType(owner: Int, type: List<Int>): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_TYPE} = :type AND ${ResourceData.COLUMN_OWNER} = -1 AND ${ResourceData.COLUMN_POSITION} = -1")
    fun getUnclaimedResourcesOfType(type: Int): List<ResourceData>?

    @Query("SELECT * FROM ${ResourceData.TABLE_NAME} WHERE ${ResourceData.COLUMN_POSITION} = :loc and ${ResourceData.COLUMN_TYPE} = :type")
    fun getResourcesAtPosOfType(loc: Int, type: Int): List<ResourceData>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addResource(vararg resourceData: ResourceData)

    @Delete
    fun removeResource(vararg setting: ResourceData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateResource(vararg setting: ResourceData)

    fun updateResourceAndIncrement(vararg setting: ResourceData) {
        updateResource(*setting.map { data -> data.version += 1 ; data }.toTypedArray())
    }
}